package org.example.visionai.infrastructure.model;

import org.example.visionai.application.PythonModelClient;
import org.example.visionai.domain.ImageBatchRequest;
import org.example.visionai.domain.ImageBatchResponse;
import org.example.visionai.domain.ImagePrediction;
import org.example.visionai.domain.ImageRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implémentation de secours du client modèle Python.
 * Pour l'instant, elle renvoie des prédictions factices afin de permettre
 * de tester l'API REST sans le microservice Python.
 */
public class StubPythonModelClient implements PythonModelClient {

    @Override
    public ImagePrediction predict(ImageRequest request) {
        return new ImagePrediction(
                request.getId(),
                "DUMMY_LABEL",
                0.0,
                Map.of("dummy_feature", (double) request.getImageBase64().length())
        );
    }

    @Override
    public ImageBatchResponse predictBatch(ImageBatchRequest request) {
        List<ImagePrediction> predictions = request.getImages().stream()
                .map(this::predict)
                .collect(Collectors.toList());
        return new ImageBatchResponse(predictions);
    }
}
