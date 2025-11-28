package org.example.visionai.application;

import org.example.visionai.domain.ImageBatchRequest;
import org.example.visionai.domain.ImageBatchResponse;
import org.example.visionai.domain.ImagePrediction;
import org.example.visionai.domain.ImageRequest;

/**
 * Port d'accès au service de modèle Python.
 * Implémentations possibles : REST, gRPC, mocks pour les tests.
 */
public interface PythonModelClient {

    ImagePrediction predict(ImageRequest request);

    ImageBatchResponse predictBatch(ImageBatchRequest request);
}
