"""
KubeWise Operator - Kopf handlers for CleanupPolicy and ResourceAudit CRDs.

This module implements the core reconciliation logic for the KubeWise operator.
It watches CleanupPolicy and ResourceAudit custom resources and performs
stale resource detection and optional cleanup.
"""

import kopf
import logging
import datetime
from typing import Optional

from prometheus_client import start_http_server

from src.detector import StaleResourceDetector
from src.metrics import METRICS

logger = logging.getLogger("kubewise")


# ---------------------------------------------------------------------------
# Startup / shutdown
# ---------------------------------------------------------------------------

@kopf.on.startup()
def startup_fn(settings: kopf.OperatorSettings, **_):
    """Configure operator settings and start Prometheus metrics server."""
    settings.posting.level = logging.INFO
    settings.persistence.finalizer = "kubewise.io/finalizer"
    settings.persistence.progress_storage = kopf.AnnotationsProgressStorage(
        prefix="kubewise.io"
    )
    # Start Prometheus metrics endpoint on port 9090
    try:
        start_http_server(9090)
        logger.info("Prometheus metrics server started on :9090")
    except OSError:
        logger.warning("Metrics server port 9090 already in use, skipping")


# ---------------------------------------------------------------------------
# CleanupPolicy handlers
# ---------------------------------------------------------------------------

@kopf.on.create("kubewise.io", "v1alpha1", "cleanuppolicies")
def on_cleanup_policy_create(spec, name, namespace, status, patch, **_):
    """Handle creation of a CleanupPolicy resource."""
    logger.info(f"CleanupPolicy '{name}' created in namespace '{namespace}'")

    patch.status["phase"] = "Active"
    patch.status["lastReconciled"] = datetime.datetime.utcnow().isoformat() + "Z"
    patch.status["staleResourcesFound"] = 0
    patch.status["resourcesCleaned"] = 0

    METRICS.policies_total.labels(namespace=namespace or "cluster").inc()

    return {"message": f"CleanupPolicy '{name}' is now active"}


@kopf.on.update("kubewise.io", "v1alpha1", "cleanuppolicies")
def on_cleanup_policy_update(spec, name, namespace, patch, **_):
    """Handle updates to a CleanupPolicy resource."""
    logger.info(f"CleanupPolicy '{name}' updated in namespace '{namespace}'")
    patch.status["lastReconciled"] = datetime.datetime.utcnow().isoformat() + "Z"
    return {"message": f"CleanupPolicy '{name}' updated"}


@kopf.on.delete("kubewise.io", "v1alpha1", "cleanuppolicies")
def on_cleanup_policy_delete(name, namespace, **_):
    """Handle deletion of a CleanupPolicy resource."""
    logger.info(f"CleanupPolicy '{name}' deleted from namespace '{namespace}'")
    METRICS.policies_total.labels(namespace=namespace or "cluster").dec()


@kopf.timer("kubewise.io", "v1alpha1", "cleanuppolicies", interval=60.0, initial_delay=10.0)
def reconcile_cleanup_policy(spec, name, namespace, status, patch, **_):
    """
    Periodic reconciliation for CleanupPolicy resources.

    Runs the stale resource detection according to the policy spec and optionally
    cleans up resources if dryRun is False.
    """
    logger.info(f"Reconciling CleanupPolicy '{name}'")

    detector = StaleResourceDetector()

    # Parse policy spec
    dry_run = spec.get("dryRun", True)
    target_namespaces = spec.get("targetNamespaces", [])
    excluded_namespaces = spec.get("excludedNamespaces", ["kube-system", "kube-public", "kube-node-lease"])
    protected_labels = spec.get("protectedLabels", {})
    resource_rules = spec.get("resources", [])

    stale_resources = []

    for rule in resource_rules:
        resource_kind = rule.get("kind", "")
        max_age_hours = rule.get("maxAgeHours", 168)  # default 7 days
        conditions = rule.get("conditions", [])

        found = detector.detect_stale(
            resource_kind=resource_kind,
            target_namespaces=target_namespaces,
            excluded_namespaces=excluded_namespaces,
            protected_labels=protected_labels,
            max_age_hours=max_age_hours,
            conditions=conditions,
        )
        stale_resources.extend(found)

    stale_count = len(stale_resources)
    cleaned_count = 0

    if not dry_run and stale_resources:
        cleaned_count = detector.cleanup(stale_resources)
        logger.info(f"Cleaned {cleaned_count}/{stale_count} stale resources for policy '{name}'")

    # Update status
    patch.status["phase"] = "Active"
    patch.status["lastReconciled"] = datetime.datetime.utcnow().isoformat() + "Z"
    patch.status["staleResourcesFound"] = stale_count
    patch.status["resourcesCleaned"] = cleaned_count
    patch.status["staleResources"] = [
        {
            "kind": r["kind"],
            "name": r["name"],
            "namespace": r["namespace"],
            "reason": r["reason"],
            "age": r["age"],
        }
        for r in stale_resources[:50]  # limit status to 50 entries
    ]

    # Update Prometheus metrics
    METRICS.stale_resources_detected.labels(
        namespace=namespace or "cluster", policy=name
    ).set(stale_count)
    METRICS.resources_cleaned.labels(
        namespace=namespace or "cluster", policy=name
    ).inc(cleaned_count)
    METRICS.last_reconcile_time.labels(
        namespace=namespace or "cluster", policy=name
    ).set_to_current_time()

    if dry_run:
        logger.info(
            f"[DRY-RUN] Policy '{name}': found {stale_count} stale resources"
        )
    else:
        logger.info(
            f"Policy '{name}': found {stale_count} stale, cleaned {cleaned_count}"
        )


# ---------------------------------------------------------------------------
# ResourceAudit handlers
# ---------------------------------------------------------------------------

@kopf.on.create("kubewise.io", "v1alpha1", "resourceaudits")
def on_resource_audit_create(spec, name, namespace, status, patch, **_):
    """
    Handle creation of a ResourceAudit resource.

    Immediately runs an on-demand audit of the cluster and populates the
    status with findings.
    """
    logger.info(f"ResourceAudit '{name}' created - running on-demand audit")

    detector = StaleResourceDetector()

    target_namespaces = spec.get("targetNamespaces", [])
    excluded_namespaces = spec.get("excludedNamespaces", ["kube-system", "kube-public", "kube-node-lease"])
    scan_types = spec.get("scanTypes", [
        "ConfigMap", "Secret", "PersistentVolumeClaim",
        "Service", "Job", "ReplicaSet"
    ])

    all_stale = []
    summary = {}

    for resource_kind in scan_types:
        found = detector.detect_stale(
            resource_kind=resource_kind,
            target_namespaces=target_namespaces,
            excluded_namespaces=excluded_namespaces,
            protected_labels={},
            max_age_hours=168,
            conditions=["orphaned"],
        )
        all_stale.extend(found)
        summary[resource_kind] = len(found)

    patch.status["phase"] = "Completed"
    patch.status["completedAt"] = datetime.datetime.utcnow().isoformat() + "Z"
    patch.status["totalStaleResources"] = len(all_stale)
    patch.status["summary"] = summary
    patch.status["findings"] = [
        {
            "kind": r["kind"],
            "name": r["name"],
            "namespace": r["namespace"],
            "reason": r["reason"],
            "age": r["age"],
        }
        for r in all_stale[:100]  # limit to 100 findings
    ]

    METRICS.audits_completed.labels(namespace=namespace or "cluster").inc()

    logger.info(
        f"ResourceAudit '{name}' completed: {len(all_stale)} stale resources found"
    )

    return {"message": f"Audit completed: {len(all_stale)} stale resources found"}
