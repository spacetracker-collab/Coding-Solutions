# KubeWise Operator

**Kubernetes Intelligent Stale Resource Cleanup Operator**

KubeWise is a Kubernetes operator that automatically detects, reports, and cleans up stale and orphaned resources in your cluster. It uses Custom Resource Definitions (CRDs) to let you define cleanup policies and trigger on-demand audits, bringing cluster hygiene management into the Kubernetes-native workflow.

---

## Table of Contents

- [Motivation & Research Background](#motivation--research-background)
- [Architecture](#architecture)
- [Features](#features)
- [Supported Resource Types](#supported-resource-types)
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Install via Helm (Recommended)](#install-via-helm-recommended)
  - [Install via Raw Manifests](#install-via-raw-manifests)
  - [Run Locally (Development)](#run-locally-development)
- [Usage](#usage)
  - [CleanupPolicy (Continuous Monitoring)](#cleanuppolicy-continuous-monitoring)
  - [ResourceAudit (On-Demand Scan)](#resourceaudit-on-demand-scan)
  - [Protecting Resources](#protecting-resources)
- [CRD Reference](#crd-reference)
  - [CleanupPolicy Spec](#cleanuppolicy-spec)
  - [ResourceAudit Spec](#resourceaudit-spec)
- [Prometheus Metrics](#prometheus-metrics)
- [Examples](#examples)
- [Docker Image](#docker-image)
- [Development](#development)
- [License](#license)

---

## Motivation & Research Background

### The Problem: Kubernetes Resource Sprawl

As Kubernetes clusters grow, they accumulate stale resources that waste storage, pollute the API server, and increase operational complexity:

- **Orphaned ConfigMaps/Secrets**: Left behind after deployments are deleted or reconfigured
- **Dangling PVCs**: Persistent Volume Claims no longer mounted by any Pod, wasting storage costs
- **Endpoint-less Services**: Services whose backing Pods have been removed
- **Completed Jobs**: Finished batch jobs that linger indefinitely
- **Old ReplicaSets**: Historical ReplicaSets from Deployment rollouts with 0 replicas

### Gap in the Ecosystem

After comprehensive research of the Kubernetes operator ecosystem (OperatorHub.io, CNCF landscape, academic literature), we identified that:

1. **Individual tools exist** for specific cleanup tasks (e.g., `kube-cleanup-operator` for Jobs, built-in `ttlSecondsAfterFinished` for Jobs)
2. **No unified, CRD-based operator** exists that covers all resource types with configurable policies
3. **Academic research** (e.g., "An Empirical Study on Kubernetes Operator Bugs" - ISSTA 2024, "AMoCNA operator" - J. Supercomputing 2025) highlights the need for autonomic resource management in cloud-native environments
4. **FinOps research** emphasizes that orphaned resources are a leading cause of cloud cost waste in Kubernetes environments

KubeWise bridges this gap by providing a **unified, declarative, CRD-based approach** to cluster resource hygiene.

### Research References

| Paper | Venue | Relevance |
|-------|-------|-----------|
| "An Empirical Study on Kubernetes Operator Bugs" | ISSTA 2024 | Operator reliability patterns |
| "AMoCNA operator: Kubernetes operator pattern for autonomic features" | J. Supercomputing 2025 | Autonomic resource management |
| "Carbon-Aware GitOps: Energy-Efficient Quality Gates" | Zenodo 2026 | Resource efficiency in K8s |
| "Enhancing Kubernetes with Load-Aware Orchestration" | SN Computer Science 2025 | Dynamic resource optimization |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                         │
│                                                              │
│  ┌──────────────────┐     ┌──────────────────────────────┐  │
│  │  CleanupPolicy    │     │     KubeWise Operator        │  │
│  │  (CRD)           │────▶│                              │  │
│  │                   │     │  ┌────────────────────────┐  │  │
│  │  - dryRun: true   │     │  │  Reconciliation Loop   │  │  │
│  │  - resources: ... │     │  │                        │  │  │
│  └──────────────────┘     │  │  1. List target NS      │  │  │
│                           │  │  2. Scan resources      │  │  │
│  ┌──────────────────┐     │  │  3. Check references    │  │  │
│  │  ResourceAudit    │     │  │  4. Flag stale ones     │  │  │
│  │  (CRD)           │────▶│  │  5. Clean (if !dryRun) │  │  │
│  │                   │     │  │  6. Update status       │  │  │
│  │  - scanTypes: ... │     │  │  7. Emit metrics       │  │  │
│  └──────────────────┘     │  └────────────────────────┘  │  │
│                           │                              │  │
│                           │  ┌────────────────────────┐  │  │
│                           │  │  Prometheus Metrics     │  │  │
│                           │  │  :9090/metrics          │  │  │
│                           │  └────────────────────────┘  │  │
│                           └──────────────────────────────┘  │
│                                                              │
│  Scanned Resources:                                          │
│  ┌──────────┐ ┌────────┐ ┌──────┐ ┌─────────┐ ┌──────────┐ │
│  │ConfigMaps│ │Secrets │ │ PVCs │ │Services │ │Jobs/RS   │ │
│  └──────────┘ └────────┘ └──────┘ └─────────┘ └──────────┘ │
└─────────────────────────────────────────────────────────────┘
```

**How it works:**

1. You create a `CleanupPolicy` CR defining which resources to scan and what "stale" means
2. The operator reconciles every 60 seconds, scanning the specified namespaces
3. For each resource type, it checks whether the resource is referenced by any active workload
4. Stale resources are reported in the CR status and exposed via Prometheus metrics
5. If `dryRun: false`, stale resources are deleted with full audit trail via Kubernetes Events

---

## Features

- **Declarative Policies**: Define cleanup rules as Kubernetes CRDs
- **Dry-Run Mode**: Safe by default - reports stale resources without deleting them
- **On-Demand Audits**: Trigger one-time cluster hygiene scans via `ResourceAudit` CR
- **Multi-Resource Support**: ConfigMaps, Secrets, PVCs, Services, Jobs, ReplicaSets
- **Namespace Filtering**: Target specific namespaces or exclude system namespaces
- **Label-Based Protection**: Protect important resources with labels
- **Prometheus Metrics**: Full observability with metrics for stale resource counts, cleanup actions, and reconciliation health
- **Kubernetes Events**: Audit trail for all cleanup actions
- **Helm Chart**: Production-ready Helm chart with RBAC, ServiceMonitor, and security contexts
- **Lightweight**: Python-based with minimal resource footprint (~128Mi memory)

---

## Supported Resource Types

| Resource | Detection Logic |
|----------|----------------|
| **ConfigMap** | Not referenced by any Pod (volumes, env, envFrom) |
| **Secret** | Not referenced by any Pod or ServiceAccount |
| **PersistentVolumeClaim** | Not mounted by any Pod |
| **Service** | No ready endpoints (no matching Pods) |
| **Job** | Completed or Failed, past configurable retention period |
| **ReplicaSet** | 0 desired/current replicas, or orphaned (no owner reference) |

---

## Installation

### Prerequisites

- Kubernetes cluster v1.20+
- `kubectl` configured to access the cluster
- Helm v3+ (for Helm installation)

### Install via Helm (Recommended)

```bash
# Add the CRDs and install the operator
helm install kubewise deploy/helm/kubewise-operator \
  --namespace kubewise-system \
  --create-namespace
```

Customize the installation:

```bash
helm install kubewise deploy/helm/kubewise-operator \
  --namespace kubewise-system \
  --create-namespace \
  --set image.tag=0.1.0 \
  --set metrics.serviceMonitor.enabled=true
```

### Install via Raw Manifests

```bash
# Install CRDs
kubectl apply -f config/crd/

# Create namespace and install RBAC + operator
kubectl create namespace kubewise-system
kubectl apply -f config/rbac/
kubectl apply -f config/manager/
```

Or use the Makefile:

```bash
make install
```

### Run Locally (Development)

```bash
# Install dependencies
pip install -r requirements.txt

# Run with your local kubeconfig
kopf run --standalone src/main.py --verbose
```

---

## Usage

### CleanupPolicy (Continuous Monitoring)

A `CleanupPolicy` defines ongoing rules for detecting stale resources. The operator reconciles every 60 seconds.

**Step 1: Create a dry-run policy to see what would be cleaned up:**

```yaml
apiVersion: kubewise.io/v1alpha1
kind: CleanupPolicy
metadata:
  name: cluster-hygiene
  namespace: kubewise-system
spec:
  dryRun: true
  excludedNamespaces:
    - kube-system
    - kube-public
  protectedLabels:
    kubewise.io/protect: "true"
  resources:
    - kind: ConfigMap
      maxAgeHours: 168    # 7 days
    - kind: Secret
      maxAgeHours: 336    # 14 days
    - kind: PersistentVolumeClaim
      maxAgeHours: 168    # 7 days
    - kind: Service
      maxAgeHours: 72     # 3 days
    - kind: Job
      maxAgeHours: 48     # 2 days
    - kind: ReplicaSet
      maxAgeHours: 168    # 7 days
```

```bash
kubectl apply -f examples/cleanup-policy-dryrun.yaml
```

**Step 2: Check what was found:**

```bash
# View the policy status
kubectl get cleanuppolicies -n kubewise-system

# Example output:
# NAME              PHASE    DRY-RUN   STALE FOUND   CLEANED   LAST RECONCILED          AGE
# cluster-hygiene   Active   true      12            0         2025-01-15T10:30:00Z     5m

# See detailed findings
kubectl get cleanuppolicy cluster-hygiene -n kubewise-system -o jsonpath='{.status.staleResources}' | jq .
```

**Step 3: Once satisfied, switch to active mode:**

```yaml
spec:
  dryRun: false  # Now it will actually delete stale resources
```

### ResourceAudit (On-Demand Scan)

A `ResourceAudit` triggers a one-time scan. Create one whenever you want a snapshot of cluster hygiene:

```yaml
apiVersion: kubewise.io/v1alpha1
kind: ResourceAudit
metadata:
  name: weekly-audit
  namespace: kubewise-system
spec:
  scanTypes:
    - ConfigMap
    - Secret
    - PersistentVolumeClaim
    - Service
    - Job
    - ReplicaSet
```

```bash
kubectl apply -f examples/resource-audit.yaml

# Check results
kubectl get resourceaudits -n kubewise-system

# Example output:
# NAME           PHASE       TOTAL STALE   COMPLETED AT             AGE
# weekly-audit   Completed   23            2025-01-15T10:35:00Z     2m

# View detailed findings
kubectl get resourceaudit weekly-audit -n kubewise-system -o yaml
```

### Protecting Resources

Add the protection label to any resource you never want flagged:

```bash
# Protect a specific ConfigMap
kubectl label configmap my-important-config kubewise.io/protect=true

# Protect all resources in a namespace
kubectl label configmap --all kubewise.io/protect=true -n production
```

Or define protected labels in the policy:

```yaml
spec:
  protectedLabels:
    kubewise.io/protect: "true"
    environment: "production"
    app.kubernetes.io/managed-by: "helm"
```

---

## CRD Reference

### CleanupPolicy Spec

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `spec.dryRun` | bool | `true` | If true, only report stale resources (no deletion) |
| `spec.targetNamespaces` | []string | `[]` (all) | Namespaces to scan (empty = all) |
| `spec.excludedNamespaces` | []string | `[kube-system, ...]` | Namespaces to skip |
| `spec.protectedLabels` | map[string]string | `{}` | Labels that protect resources |
| `spec.resources` | []ResourceRule | required | List of resource rules |
| `spec.resources[].kind` | string | required | Resource kind to scan |
| `spec.resources[].maxAgeHours` | int | `168` | Max age (hours) before flagging as stale |
| `spec.resources[].conditions` | []string | `[]` | Extra conditions (e.g., "orphaned") |

**Status Fields:**

| Field | Description |
|-------|-------------|
| `status.phase` | Current phase: `Active` or `Error` |
| `status.lastReconciled` | Timestamp of last reconciliation |
| `status.staleResourcesFound` | Number of stale resources detected |
| `status.resourcesCleaned` | Number of resources cleaned (0 if dry-run) |
| `status.staleResources` | List of stale resources (max 50) |

### ResourceAudit Spec

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `spec.targetNamespaces` | []string | `[]` (all) | Namespaces to scan |
| `spec.excludedNamespaces` | []string | `[kube-system, ...]` | Namespaces to skip |
| `spec.scanTypes` | []string | all types | Resource types to scan |

**Status Fields:**

| Field | Description |
|-------|-------------|
| `status.phase` | `Running`, `Completed`, or `Error` |
| `status.completedAt` | Timestamp when audit finished |
| `status.totalStaleResources` | Total stale resources found |
| `status.summary` | Map of resource kind to count |
| `status.findings` | Detailed list of findings (max 100) |

---

## Prometheus Metrics

The operator exposes metrics on port 9090 at `/metrics`:

| Metric | Type | Labels | Description |
|--------|------|--------|-------------|
| `kubewise_cleanup_policies_total` | Gauge | namespace | Number of active CleanupPolicy resources |
| `kubewise_stale_resources_detected` | Gauge | namespace, policy | Stale resources detected per policy |
| `kubewise_resources_cleaned_total` | Counter | namespace, policy | Total resources cleaned |
| `kubewise_last_reconcile_timestamp` | Gauge | namespace, policy | Timestamp of last reconciliation |
| `kubewise_reconcile_duration_seconds` | Summary | namespace, policy | Reconciliation duration |
| `kubewise_audits_completed_total` | Counter | namespace | Total ResourceAudit scans completed |
| `kubewise_errors_total` | Counter | type | Total errors encountered |

**Grafana Dashboard Query Examples:**

```promql
# Total stale resources across all policies
sum(kubewise_stale_resources_detected)

# Cleanup rate over time
rate(kubewise_resources_cleaned_total[1h])

# Time since last reconciliation
time() - kubewise_last_reconcile_timestamp
```

---

## Examples

The `examples/` directory contains ready-to-use CR manifests:

| File | Description |
|------|-------------|
| [`cleanup-policy-dryrun.yaml`](examples/cleanup-policy-dryrun.yaml) | Safe dry-run policy scanning all namespaces |
| [`cleanup-policy-active.yaml`](examples/cleanup-policy-active.yaml) | Active cleanup for dev/staging namespaces |
| [`resource-audit.yaml`](examples/resource-audit.yaml) | Full cluster audit scanning all resource types |
| [`resource-audit-targeted.yaml`](examples/resource-audit-targeted.yaml) | Targeted audit for specific namespace and types |

**Quick start:**

```bash
# Apply a dry-run policy
kubectl apply -f examples/cleanup-policy-dryrun.yaml

# Wait for first reconciliation (~60 seconds)
sleep 70

# Check results
kubectl get cleanuppolicies -n kubewise-system
kubectl get cleanuppolicy cluster-hygiene-dryrun -n kubewise-system -o yaml

# Run an on-demand audit
kubectl apply -f examples/resource-audit.yaml
kubectl get resourceaudits -n kubewise-system
```

---

## Docker Image

The operator is packaged as a Docker image:

```bash
# Pull the image
docker pull ghcr.io/spacetracker-collab/kubewise-operator:0.1.0

# Build locally
docker build -t kubewise-operator:latest .

# Run locally (for testing)
docker run --rm -v ~/.kube/config:/root/.kube/config kubewise-operator:latest
```

**Image details:**
- Base: `python:3.11-slim`
- Size: ~120MB (multi-stage build)
- User: non-root (UID 1000)
- Ports: 9090 (metrics), 8080 (health)

---

## Development

```bash
# Clone the repository
git clone https://github.com/spacetracker-collab/kubewise-operator.git
cd kubewise-operator

# Install dependencies
pip install -r requirements.txt

# Run locally against your cluster
make run

# Lint the code
make lint

# Build Docker image
make build

# Push Docker image
make push
```

### Project Structure

```
kubewise-operator/
├── src/
│   ├── __init__.py
│   ├── main.py          # Entry point, imports handlers
│   ├── handlers.py      # Kopf handlers for CRD reconciliation
│   ├── detector.py      # Stale resource detection engine
│   └── metrics.py       # Prometheus metrics definitions
├── config/
│   ├── crd/             # Custom Resource Definitions
│   ├── rbac/            # RBAC (ClusterRole, Binding, ServiceAccount)
│   └── manager/         # Operator Deployment manifest
├── deploy/
│   └── helm/
│       └── kubewise-operator/  # Helm chart
├── examples/            # Example CR manifests
├── docs/                # Additional documentation
├── Dockerfile           # Multi-stage Docker build
├── Makefile             # Build and deployment targets
├── requirements.txt     # Python dependencies
└── README.md            # This file
```

---

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
