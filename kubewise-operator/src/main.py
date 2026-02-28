"""
KubeWise Operator - Main Entry Point.

A Kubernetes operator for intelligent stale resource detection and cleanup.
"""

import logging
import kopf

# Import handlers to register them with kopf
import src.handlers  # noqa: F401

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)

logger = logging.getLogger("kubewise")
logger.info("KubeWise Operator starting...")
