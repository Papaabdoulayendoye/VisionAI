package org.example.visionai.application;

import org.example.visionai.domain.ImageBatchRequest;
import org.example.visionai.domain.ImageBatchResponse;
import org.example.visionai.domain.ImagePrediction;
import org.example.visionai.domain.ImageRequest;

import java.util.List;
import java.util.Objects;

/**
 * Service d'orchestration du pipeline VisionAI côté Java.
 * Pour l'instant il délègue simplement au client de modèle Python.
 * La logique de batch, de cache, etc. sera ajoutée plus tard.
 */
public class VisionPipelineService {

    private final PythonModelClient pythonModelClient;

    public VisionPipelineService(PythonModelClient pythonModelClient) {
        this.pythonModelClient = Objects.requireNonNull(pythonModelClient, "pythonModelClient must not be null");
    }

    public ImagePrediction predict(ImageRequest request) {
        return pythonModelClient.predict(request);
    }

    public ImageBatchResponse predictBatch(ImageBatchRequest request) {
        return pythonModelClient.predictBatch(request);
    }

    public ImageBatchResponse predictBatch(List<ImageRequest> requests) {
        return predictBatch(new ImageBatchRequest(requests));
    }
}
