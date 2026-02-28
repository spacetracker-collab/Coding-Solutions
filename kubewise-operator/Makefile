# KubeWise Operator - Makefile

IMAGE_REPO ?= ghcr.io/spacetracker-collab/kubewise-operator
IMAGE_TAG ?= 0.1.0
NAMESPACE ?= kubewise-system

.PHONY: help build push install install-crds install-operator uninstall run lint test

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

## --- Docker ---

build: ## Build the Docker image
	docker build -t $(IMAGE_REPO):$(IMAGE_TAG) -t $(IMAGE_REPO):latest .

push: ## Push the Docker image to registry
	docker push $(IMAGE_REPO):$(IMAGE_TAG)
	docker push $(IMAGE_REPO):latest

## --- Installation (raw manifests) ---

install: install-crds install-operator ## Install everything (CRDs + operator)

install-crds: ## Install CRDs
	kubectl apply -f config/crd/

install-operator: ## Install operator deployment and RBAC
	kubectl create namespace $(NAMESPACE) --dry-run=client -o yaml | kubectl apply -f -
	kubectl apply -f config/rbac/
	kubectl apply -f config/manager/

uninstall: ## Uninstall everything
	kubectl delete -f config/manager/ --ignore-not-found
	kubectl delete -f config/rbac/ --ignore-not-found
	kubectl delete -f config/crd/ --ignore-not-found
	kubectl delete namespace $(NAMESPACE) --ignore-not-found

## --- Installation (Helm) ---

helm-install: ## Install via Helm
	helm install kubewise deploy/helm/kubewise-operator \
		--namespace $(NAMESPACE) --create-namespace

helm-upgrade: ## Upgrade via Helm
	helm upgrade kubewise deploy/helm/kubewise-operator \
		--namespace $(NAMESPACE)

helm-uninstall: ## Uninstall via Helm
	helm uninstall kubewise --namespace $(NAMESPACE)

## --- Development ---

run: ## Run the operator locally (requires kubeconfig)
	pip install -r requirements.txt
	kopf run --standalone src/main.py --verbose

lint: ## Run linters
	pip install ruff
	ruff check src/

test: ## Run unit tests
	pip install pytest
	pytest tests/ -v

## --- Examples ---

apply-examples: ## Apply example CRs
	kubectl apply -f examples/

delete-examples: ## Delete example CRs
	kubectl delete -f examples/ --ignore-not-found
