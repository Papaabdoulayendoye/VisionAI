import base64
from typing import Dict

import cv2
import numpy as np


def _decode_image(image_base64: str) -> np.ndarray:
    """Decode a base64-encoded image into an OpenCV BGR image."""
    raw = base64.b64decode(image_base64)
    np_arr = np.frombuffer(raw, dtype=np.uint8)
    image = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
    if image is None:
        raise ValueError("Unable to decode image")
    return image


def extract_features(image_base64: str) -> Dict[str, float]:
    """Extract simple features from an image using OpenCV.

    Features:
      - width, height
      - aspect_ratio
      - mean_b, mean_g, mean_r (average color channels)
    """
    image = _decode_image(image_base64)
    height, width = image.shape[:2]
    aspect_ratio = float(width) / float(height) if height > 0 else 0.0

    mean_b, mean_g, mean_r, _ = cv2.mean(image)

    return {
        "width": float(width),
        "height": float(height),
        "aspect_ratio": float(aspect_ratio),
        "mean_b": float(mean_b),
        "mean_g": float(mean_g),
        "mean_r": float(mean_r),
    }


def classify(features: Dict[str, float]) -> Dict[str, float | str]:
    """Very simple heuristic classifier based on brightness.

    Returns a dict with keys: label, score.
    """
    mean_b = features.get("mean_b", 0.0)
    mean_g = features.get("mean_g", 0.0)
    mean_r = features.get("mean_r", 0.0)
    brightness = (mean_b + mean_g + mean_r) / 3.0

    # Normalize brightness into [0, 1] as a confidence-like score
    score = max(0.0, min(1.0, brightness / 255.0))

    label = "BRIGHT" if brightness >= 127.5 else "DARK"

    return {"label": label, "score": float(score)}


def predict(image_id: str, image_base64: str) -> Dict[str, object]:
    """Full prediction pipeline for a single image."""
    features = extract_features(image_base64)
    result = classify(features)
    return {
        "id": image_id,
        "label": result["label"],
        "score": result["score"],
        "features": features,
    }
