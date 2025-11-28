# VisionAI Python Microservice

This directory contains the Python microservice used by the Java Spring Boot backend
for image feature extraction and lightweight classification.

## Stack
- FastAPI for the HTTP API
- OpenCV + NumPy for image processing

## Endpoints
- `GET /health` – simple health-check
- `POST /predict` – single image prediction
- `POST /predict-batch` – batch image prediction

Request/response schemas are aligned with the Java domain models:
- `ImageRequest` / `ImageBatchRequest`
- `ImagePrediction` / `ImageBatchResponse`

## Local development

Create a virtual environment and install dependencies:

```bash
python -m venv .venv
source .venv/bin/activate  # On Windows: .venv\\Scripts\\activate
pip install -r requirements.txt
```

Run the service locally:

```bash
uvicorn main:app --host 0.0.0.0 --port 8001 --reload
```

The Java service can then reach the Python model at `http://localhost:8001`.
