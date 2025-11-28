from typing import List, Dict

from pydantic import BaseModel


class ImageRequest(BaseModel):
    id: str
    imageBase64: str


class ImageBatchRequest(BaseModel):
    images: List[ImageRequest]


class ImagePrediction(BaseModel):
    id: str
    label: str
    score: float
    features: Dict[str, float]


class ImageBatchResponse(BaseModel):
    predictions: List[ImagePrediction]
