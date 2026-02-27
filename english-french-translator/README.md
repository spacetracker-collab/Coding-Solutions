# English to French Translator

A Python program that translates English words/sentences to French using the [Helsinki-NLP/opus-mt-en-fr](https://huggingface.co/Helsinki-NLP/opus-mt-en-fr) Hugging Face model.

## Local Usage

```bash
pip install -r requirements.txt
python translator.py "hello"
python translator.py --interactive
```

## Docker

### Build

```bash
docker build -t spacetracker/english-french-translator:latest .
```

### Run

```bash
# Translate a single word/sentence
docker run --rm spacetracker/english-french-translator:latest "hello"

# Interactive mode
docker run --rm -it spacetracker/english-french-translator:latest --interactive
```

### Push to Docker Hub

```bash
docker login
docker push spacetracker/english-french-translator:latest
```

## Kubernetes Deployment

### Run as a one-off Job

```bash
kubectl apply -f k8s/job.yaml
```

### Run as an interactive Deployment

```bash
kubectl apply -f k8s/deployment.yaml
kubectl attach -it deployment/english-french-translator
```
