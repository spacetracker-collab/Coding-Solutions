FROM python:3.11-slim AS builder

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir --prefix=/install -r requirements.txt

FROM python:3.11-slim

LABEL org.opencontainers.image.title="KubeWise Operator"
LABEL org.opencontainers.image.description="Kubernetes Intelligent Stale Resource Cleanup Operator"
LABEL org.opencontainers.image.source="https://github.com/spacetracker-collab/kubewise-operator"
LABEL org.opencontainers.image.version="0.1.0"
LABEL org.opencontainers.image.licenses="Apache-2.0"

WORKDIR /app

COPY --from=builder /install /usr/local
COPY src/ src/

RUN useradd -r -u 1000 kubewise
USER 1000

ENTRYPOINT ["kopf", "run", "--standalone", "--liveness=http://0.0.0.0:8080/healthz", "src/main.py"]
