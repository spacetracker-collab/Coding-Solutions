"""
KubeWise Operator - Prometheus Metrics.

Exposes metrics for monitoring operator health and cluster hygiene.
"""

from prometheus_client import Counter, Gauge, Summary


class KubeWiseMetrics:
    """Prometheus metrics for the KubeWise operator."""

    def __init__(self):
        # Policy metrics
        self.policies_total = Gauge(
            "kubewise_cleanup_policies_total",
            "Total number of active CleanupPolicy resources",
            ["namespace"],
        )

        # Detection metrics
        self.stale_resources_detected = Gauge(
            "kubewise_stale_resources_detected",
            "Number of stale resources detected per policy",
            ["namespace", "policy"],
        )

        # Cleanup metrics
        self.resources_cleaned = Counter(
            "kubewise_resources_cleaned_total",
            "Total number of resources cleaned up",
            ["namespace", "policy"],
        )

        # Reconciliation metrics
        self.last_reconcile_time = Gauge(
            "kubewise_last_reconcile_timestamp",
            "Timestamp of the last reconciliation",
            ["namespace", "policy"],
        )

        self.reconcile_duration = Summary(
            "kubewise_reconcile_duration_seconds",
            "Duration of reconciliation in seconds",
            ["namespace", "policy"],
        )

        # Audit metrics
        self.audits_completed = Counter(
            "kubewise_audits_completed_total",
            "Total number of ResourceAudit scans completed",
            ["namespace"],
        )

        # Error metrics
        self.errors_total = Counter(
            "kubewise_errors_total",
            "Total number of errors encountered",
            ["type"],
        )


# Singleton metrics instance
METRICS = KubeWiseMetrics()
