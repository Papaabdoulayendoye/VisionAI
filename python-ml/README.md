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

Run the REST service locally:

```bash
uvicorn main:app --host 0.0.0.0 --port 8001 --reload
```

The Java service can then reach the Python model at `http://localhost:8001` when `visionai.python.client-mode=rest`.

## gRPC server

To use the gRPC integration, first generate Python gRPC stubs from the shared `visionai.proto` file:

```bash
python -m grpc_tools.protoc \
  -I ../src/main/proto \
  --python_out=. \
  --grpc_python_out=. \
  ../src/main/proto/visionai.proto
```

Then start the gRPC server:

```bash
python grpc_server.py
```

On the Java side, configure:

```properties
visionai.python.client-mode=grpc
visionai.python.grpc.host=localhost
visionai.python.grpc.port=50051
```
