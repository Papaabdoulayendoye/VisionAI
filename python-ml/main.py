from typing import List

from fastapi import FastAPI, HTTPException

from model import predict
from schemas import (
    ImageRequest,
    ImageBatchRequest,
    ImagePrediction,
    ImageBatchResponse,
)

app = FastAPI(title="VisionAI Python Model Service")


@app.get("/health")
async def health() -> dict:
    return {"status": "ok"}


@app.post("/predict", response_model=ImagePrediction)
async def predict_single(request: ImageRequest) -> ImagePrediction:
    try:
        result = predict(request.id, request.imageBase64)
    except Exception as exc:  # noqa: BLE001
        raise HTTPException(status_code=400, detail=str(exc)) from exc
    return ImagePrediction(**result)


@app.post("/predict-batch", response_model=ImageBatchResponse)
async def predict_batch(request: ImageBatchRequest) -> ImageBatchResponse:
    predictions: List[ImagePrediction] = []
    for img in request.images:
        try:
            result = predict(img.id, img.imageBase64)
            predictions.append(ImagePrediction(**result))
        except Exception as exc:  # noqa: BLE001
            raise HTTPException(status_code=400, detail=str(exc)) from exc
    return ImageBatchResponse(predictions=predictions)
