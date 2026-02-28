"""
KubeWise Operator - Stale Resource Detection Engine.

This module contains the logic for detecting stale/orphaned Kubernetes resources.
It checks various resource types against configurable rules to determine if they
are still in active use.
"""

import logging
import datetime
from typing import Dict, List, Optional, Set

from kubernetes import client, config
from kubernetes.client.rest import ApiException

logger = logging.getLogger("kubewise")


class StaleResourceDetector:
    """
    Detects stale and orphaned Kubernetes resources.

    Supports detection of:
    - ConfigMaps not referenced by any workload
    - Secrets not referenced by any workload
    - PVCs not mounted by any Pod
    - Services with no matching endpoints
    - Completed/Failed Jobs past retention
    - Orphaned ReplicaSets with 0 replicas
    """

    def __init__(self):
        try:
            config.load_incluster_config()
        except config.ConfigException:
            try:
                config.load_kube_config()
            except config.ConfigException:
                logger.warning("Could not load kube config - running in standalone mode")
                return

        self.core_v1 = client.CoreV1Api()
        self.apps_v1 = client.AppsV1Api()
        self.batch_v1 = client.BatchV1Api()

    def detect_stale(
        self,
        resource_kind: str,
        target_namespaces: List[str],
        excluded_namespaces: List[str],
        protected_labels: Dict[str, str],
        max_age_hours: int = 168,
        conditions: Optional[List[str]] = None,
    ) -> List[Dict]:
        """
        Detect stale resources of a given kind.

        Args:
            resource_kind: The kind of resource to check (ConfigMap, Secret, etc.)
            target_namespaces: Namespaces to scan (empty = all namespaces)
            excluded_namespaces: Namespaces to exclude from scanning
            protected_labels: Labels that protect resources from being flagged
            max_age_hours: Maximum age in hours before considering stale
            conditions: Additional conditions for staleness detection

        Returns:
            List of dicts describing stale resources found
        """
        detection_map = {
            "ConfigMap": self._detect_stale_configmaps,
            "Secret": self._detect_stale_secrets,
            "PersistentVolumeClaim": self._detect_stale_pvcs,
            "Service": self._detect_stale_services,
            "Job": self._detect_stale_jobs,
            "ReplicaSet": self._detect_stale_replicasets,
        }

        detect_fn = detection_map.get(resource_kind)
        if detect_fn is None:
            logger.warning(f"Unsupported resource kind: {resource_kind}")
            return []

        namespaces = self._resolve_namespaces(target_namespaces, excluded_namespaces)
        all_stale = []

        for ns in namespaces:
            try:
                stale = detect_fn(
                    namespace=ns,
                    protected_labels=protected_labels,
                    max_age_hours=max_age_hours,
                )
                all_stale.extend(stale)
            except ApiException as e:
                logger.error(f"API error scanning {resource_kind} in {ns}: {e.reason}")
            except Exception as e:
                logger.error(f"Error scanning {resource_kind} in {ns}: {e}")

        return all_stale

    def cleanup(self, stale_resources: List[Dict]) -> int:
        """
        Delete the given stale resources.

        Args:
            stale_resources: List of stale resource dicts from detect_stale()

        Returns:
            Number of resources successfully deleted
        """
        cleaned = 0
        for resource in stale_resources:
            try:
                self._delete_resource(resource)
                cleaned += 1
                logger.info(
                    f"Deleted {resource['kind']}/{resource['name']} "
                    f"in namespace {resource['namespace']}"
                )
            except ApiException as e:
                logger.error(
                    f"Failed to delete {resource['kind']}/{resource['name']}: {e.reason}"
                )
            except Exception as e:
                logger.error(
                    f"Error deleting {resource['kind']}/{resource['name']}: {e}"
                )
        return cleaned

    # -----------------------------------------------------------------------
    # Private: namespace resolution
    # -----------------------------------------------------------------------

    def _resolve_namespaces(
        self,
        target_namespaces: List[str],
        excluded_namespaces: List[str],
    ) -> List[str]:
        """Resolve the list of namespaces to scan."""
        if target_namespaces:
            return [ns for ns in target_namespaces if ns not in excluded_namespaces]

        try:
            ns_list = self.core_v1.list_namespace()
            return [
                ns.metadata.name
                for ns in ns_list.items
                if ns.metadata.name not in excluded_namespaces
            ]
        except ApiException:
            logger.error("Failed to list namespaces")
            return []

    # -----------------------------------------------------------------------
    # Private: resource reference collection
    # -----------------------------------------------------------------------

    def _get_referenced_configmaps(self, namespace: str) -> Set[str]:
        """Get all ConfigMap names referenced by pods in a namespace."""
        referenced = set()
        try:
            pods = self.core_v1.list_namespaced_pod(namespace)
            for pod in pods.items:
                spec = pod.spec
                if spec is None:
                    continue

                # Check volumes
                if spec.volumes:
                    for vol in spec.volumes:
                        if vol.config_map and vol.config_map.name:
                            referenced.add(vol.config_map.name)
                        if vol.projected and vol.projected.sources:
                            for src in vol.projected.sources:
                                if src.config_map and src.config_map.name:
                                    referenced.add(src.config_map.name)

                # Check env and envFrom in containers
                containers = list(spec.containers or [])
                if spec.init_containers:
                    containers.extend(spec.init_containers)

                for container in containers:
                    if container.env:
                        for env in container.env:
                            if (
                                env.value_from
                                and env.value_from.config_map_key_ref
                                and env.value_from.config_map_key_ref.name
                            ):
                                referenced.add(env.value_from.config_map_key_ref.name)
                    if container.env_from:
                        for env_from in container.env_from:
                            if env_from.config_map_ref and env_from.config_map_ref.name:
                                referenced.add(env_from.config_map_ref.name)
        except ApiException as e:
            logger.error(f"Error listing pods in {namespace}: {e.reason}")

        return referenced

    def _get_referenced_secrets(self, namespace: str) -> Set[str]:
        """Get all Secret names referenced by pods in a namespace."""
        referenced = set()
        try:
            pods = self.core_v1.list_namespaced_pod(namespace)
            for pod in pods.items:
                spec = pod.spec
                if spec is None:
                    continue

                # Check volumes
                if spec.volumes:
                    for vol in spec.volumes:
                        if vol.secret and vol.secret.secret_name:
                            referenced.add(vol.secret.secret_name)
                        if vol.projected and vol.projected.sources:
                            for src in vol.projected.sources:
                                if src.secret and src.secret.name:
                                    referenced.add(src.secret.name)

                # Check env and envFrom in containers
                containers = list(spec.containers or [])
                if spec.init_containers:
                    containers.extend(spec.init_containers)

                for container in containers:
                    if container.env:
                        for env in container.env:
                            if (
                                env.value_from
                                and env.value_from.secret_key_ref
                                and env.value_from.secret_key_ref.name
                            ):
                                referenced.add(env.value_from.secret_key_ref.name)
                    if container.env_from:
                        for env_from in container.env_from:
                            if env_from.secret_ref and env_from.secret_ref.name:
                                referenced.add(env_from.secret_ref.name)

            # Also check service accounts for image pull secrets
            sa_list = self.core_v1.list_namespaced_service_account(namespace)
            for sa in sa_list.items:
                if sa.secrets:
                    for s in sa.secrets:
                        referenced.add(s.name)
                if sa.image_pull_secrets:
                    for s in sa.image_pull_secrets:
                        referenced.add(s.name)

        except ApiException as e:
            logger.error(f"Error listing pods/SAs in {namespace}: {e.reason}")

        return referenced

    def _get_mounted_pvcs(self, namespace: str) -> Set[str]:
        """Get all PVC names mounted by pods in a namespace."""
        mounted = set()
        try:
            pods = self.core_v1.list_namespaced_pod(namespace)
            for pod in pods.items:
                if pod.spec and pod.spec.volumes:
                    for vol in pod.spec.volumes:
                        if (
                            vol.persistent_volume_claim
                            and vol.persistent_volume_claim.claim_name
                        ):
                            mounted.add(vol.persistent_volume_claim.claim_name)
        except ApiException as e:
            logger.error(f"Error listing pods in {namespace}: {e.reason}")
        return mounted

    # -----------------------------------------------------------------------
    # Private: age calculation
    # -----------------------------------------------------------------------

    @staticmethod
    def _resource_age_hours(resource) -> float:
        """Calculate the age of a resource in hours."""
        if (
            resource.metadata
            and resource.metadata.creation_timestamp
        ):
            created = resource.metadata.creation_timestamp
            if created.tzinfo is not None:
                created = created.replace(tzinfo=None)
            age = datetime.datetime.utcnow() - created
            return age.total_seconds() / 3600.0
        return 0.0

    @staticmethod
    def _format_age(hours: float) -> str:
        """Format age in hours to a human-readable string."""
        if hours < 1:
            return f"{int(hours * 60)}m"
        if hours < 24:
            return f"{int(hours)}h"
        days = int(hours / 24)
        return f"{days}d"

    def _is_protected(self, resource, protected_labels: Dict[str, str]) -> bool:
        """Check if a resource is protected by labels."""
        if not protected_labels:
            return False
        labels = resource.metadata.labels or {}
        for key, value in protected_labels.items():
            if labels.get(key) == value:
                return True
        return False

    # -----------------------------------------------------------------------
    # Private: detection per resource type
    # -----------------------------------------------------------------------

    def _detect_stale_configmaps(
        self, namespace: str, protected_labels: Dict, max_age_hours: int
    ) -> List[Dict]:
        """Detect ConfigMaps not referenced by any workload."""
        stale = []
        referenced = self._get_referenced_configmaps(namespace)
        configmaps = self.core_v1.list_namespaced_config_map(namespace)

        for cm in configmaps.items:
            name = cm.metadata.name
            # Skip system configmaps
            if name.startswith("kube-") or name == "kubernetes":
                continue
            if self._is_protected(cm, protected_labels):
                continue

            age_hours = self._resource_age_hours(cm)
            if age_hours < max_age_hours:
                continue

            if name not in referenced:
                stale.append({
                    "kind": "ConfigMap",
                    "name": name,
                    "namespace": namespace,
                    "reason": "Not referenced by any Pod, Deployment, or StatefulSet",
                    "age": self._format_age(age_hours),
                    "age_hours": age_hours,
                })

        return stale

    def _detect_stale_secrets(
        self, namespace: str, protected_labels: Dict, max_age_hours: int
    ) -> List[Dict]:
        """Detect Secrets not referenced by any workload."""
        stale = []
        referenced = self._get_referenced_secrets(namespace)
        secrets = self.core_v1.list_namespaced_secret(namespace)

        for secret in secrets.items:
            name = secret.metadata.name
            # Skip service account tokens and system secrets
            secret_type = secret.type or ""
            if secret_type in (
                "kubernetes.io/service-account-token",
                "kubernetes.io/dockercfg",
                "kubernetes.io/dockerconfigjson",
                "bootstrap.kubernetes.io/token",
            ):
                continue
            if self._is_protected(secret, protected_labels):
                continue

            age_hours = self._resource_age_hours(secret)
            if age_hours < max_age_hours:
                continue

            if name not in referenced:
                stale.append({
                    "kind": "Secret",
                    "name": name,
                    "namespace": namespace,
                    "reason": "Not referenced by any Pod or ServiceAccount",
                    "age": self._format_age(age_hours),
                    "age_hours": age_hours,
                })

        return stale

    def _detect_stale_pvcs(
        self, namespace: str, protected_labels: Dict, max_age_hours: int
    ) -> List[Dict]:
        """Detect PVCs not mounted by any Pod."""
        stale = []
        mounted = self._get_mounted_pvcs(namespace)
        pvcs = self.core_v1.list_namespaced_persistent_volume_claim(namespace)

        for pvc in pvcs.items:
            name = pvc.metadata.name
            if self._is_protected(pvc, protected_labels):
                continue

            age_hours = self._resource_age_hours(pvc)
            if age_hours < max_age_hours:
                continue

            if name not in mounted:
                phase = pvc.status.phase if pvc.status else "Unknown"
                storage = "unknown"
                if pvc.spec and pvc.spec.resources and pvc.spec.resources.requests:
                    storage = pvc.spec.resources.requests.get("storage", "unknown")
                stale.append({
                    "kind": "PersistentVolumeClaim",
                    "name": name,
                    "namespace": namespace,
                    "reason": f"Not mounted by any Pod (phase: {phase}, size: {storage})",
                    "age": self._format_age(age_hours),
                    "age_hours": age_hours,
                })

        return stale

    def _detect_stale_services(
        self, namespace: str, protected_labels: Dict, max_age_hours: int
    ) -> List[Dict]:
        """Detect Services with no backing endpoints."""
        stale = []
        services = self.core_v1.list_namespaced_service(namespace)

        for svc in services.items:
            name = svc.metadata.name
            # Skip the default kubernetes service
            if name == "kubernetes" and namespace == "default":
                continue
            # Skip headless services (often used by StatefulSets)
            if svc.spec and svc.spec.cluster_ip == "None":
                continue
            # Skip ExternalName services
            if svc.spec and svc.spec.type == "ExternalName":
                continue
            if self._is_protected(svc, protected_labels):
                continue

            age_hours = self._resource_age_hours(svc)
            if age_hours < max_age_hours:
                continue

            # Check if the service has endpoints
            try:
                endpoints = self.core_v1.read_namespaced_endpoints(name, namespace)
                has_endpoints = False
                if endpoints.subsets:
                    for subset in endpoints.subsets:
                        if subset.addresses and len(subset.addresses) > 0:
                            has_endpoints = True
                            break

                if not has_endpoints:
                    stale.append({
                        "kind": "Service",
                        "name": name,
                        "namespace": namespace,
                        "reason": "No ready endpoints found",
                        "age": self._format_age(age_hours),
                        "age_hours": age_hours,
                    })
            except ApiException as e:
                if e.status == 404:
                    stale.append({
                        "kind": "Service",
                        "name": name,
                        "namespace": namespace,
                        "reason": "No Endpoints object found",
                        "age": self._format_age(age_hours),
                        "age_hours": age_hours,
                    })

        return stale

    def _detect_stale_jobs(
        self, namespace: str, protected_labels: Dict, max_age_hours: int
    ) -> List[Dict]:
        """Detect completed or failed Jobs past retention."""
        stale = []
        jobs = self.batch_v1.list_namespaced_job(namespace)

        for job in jobs.items:
            name = job.metadata.name
            if self._is_protected(job, protected_labels):
                continue

            # Only consider completed or failed jobs
            if not job.status:
                continue

            is_complete = False
            job_status = "Unknown"
            completion_time = None

            if job.status.succeeded and job.status.succeeded > 0:
                is_complete = True
                job_status = "Succeeded"
                completion_time = job.status.completion_time
            elif job.status.failed and job.status.failed > 0:
                conditions = job.status.conditions or []
                for cond in conditions:
                    if cond.type == "Failed" and cond.status == "True":
                        is_complete = True
                        job_status = "Failed"
                        completion_time = cond.last_transition_time
                        break

            if not is_complete:
                continue

            # Calculate age from completion time
            if completion_time:
                if completion_time.tzinfo is not None:
                    completion_time = completion_time.replace(tzinfo=None)
                age = datetime.datetime.utcnow() - completion_time
                age_hours = age.total_seconds() / 3600.0
            else:
                age_hours = self._resource_age_hours(job)

            if age_hours < max_age_hours:
                continue

            stale.append({
                "kind": "Job",
                "name": name,
                "namespace": namespace,
                "reason": f"Job {job_status} and past retention ({self._format_age(max_age_hours)})",
                "age": self._format_age(age_hours),
                "age_hours": age_hours,
            })

        return stale

    def _detect_stale_replicasets(
        self, namespace: str, protected_labels: Dict, max_age_hours: int
    ) -> List[Dict]:
        """Detect orphaned ReplicaSets with 0 replicas and no owner."""
        stale = []
        replicasets = self.apps_v1.list_namespaced_replica_set(namespace)

        for rs in replicasets.items:
            name = rs.metadata.name
            if self._is_protected(rs, protected_labels):
                continue

            # Skip ReplicaSets that have owner references (managed by Deployments)
            if rs.metadata.owner_references:
                # Only flag RS with 0 replicas managed by Deployments
                # that have been scaled down for a long time
                desired = rs.spec.replicas if rs.spec and rs.spec.replicas is not None else 0
                current = rs.status.replicas if rs.status and rs.status.replicas is not None else 0
                if desired == 0 and current == 0:
                    age_hours = self._resource_age_hours(rs)
                    if age_hours >= max_age_hours:
                        stale.append({
                            "kind": "ReplicaSet",
                            "name": name,
                            "namespace": namespace,
                            "reason": "Old ReplicaSet with 0 desired and 0 current replicas",
                            "age": self._format_age(age_hours),
                            "age_hours": age_hours,
                        })
            else:
                # Truly orphaned - no owner reference
                age_hours = self._resource_age_hours(rs)
                if age_hours >= max_age_hours:
                    stale.append({
                        "kind": "ReplicaSet",
                        "name": name,
                        "namespace": namespace,
                        "reason": "Orphaned ReplicaSet with no owner reference",
                        "age": self._format_age(age_hours),
                        "age_hours": age_hours,
                    })

        return stale

    # -----------------------------------------------------------------------
    # Private: resource deletion
    # -----------------------------------------------------------------------

    def _delete_resource(self, resource: Dict):
        """Delete a single Kubernetes resource."""
        kind = resource["kind"]
        name = resource["name"]
        namespace = resource["namespace"]

        delete_map = {
            "ConfigMap": lambda: self.core_v1.delete_namespaced_config_map(name, namespace),
            "Secret": lambda: self.core_v1.delete_namespaced_secret(name, namespace),
            "PersistentVolumeClaim": lambda: self.core_v1.delete_namespaced_persistent_volume_claim(name, namespace),
            "Service": lambda: self.core_v1.delete_namespaced_service(name, namespace),
            "Job": lambda: self.batch_v1.delete_namespaced_job(
                name, namespace, propagation_policy="Background"
            ),
            "ReplicaSet": lambda: self.apps_v1.delete_namespaced_replica_set(name, namespace),
        }

        delete_fn = delete_map.get(kind)
        if delete_fn:
            delete_fn()
        else:
            raise ValueError(f"Cannot delete resource of kind: {kind}")
