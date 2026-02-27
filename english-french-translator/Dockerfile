FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc g++ && \
    rm -rf /var/lib/apt/lists/*

# Copy and install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Pre-download the translation model so it's baked into the image
RUN python -c "from transformers import pipeline; pipeline('translation_en_to_fr', model='Helsinki-NLP/opus-mt-en-fr')"

# Copy application code
COPY translator.py .

ENTRYPOINT ["python", "translator.py"]
