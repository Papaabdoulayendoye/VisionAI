package org.example.visionai.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Réponse de prédiction pour un batch d'images.
 */
public final class ImageBatchResponse {

    private final List<ImagePrediction> predictions;

    public ImageBatchResponse(List<ImagePrediction> predictions) {
        this.predictions = predictions == null ? List.of() : List.copyOf(predictions);
    }

    public List<ImagePrediction> getPredictions() {
        return Collections.unmodifiableList(predictions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageBatchResponse that = (ImageBatchResponse) o;
        return Objects.equals(predictions, that.predictions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predictions);
    }

    @Override
    public String toString() {
        return "ImageBatchResponse{" +
                "predictionsCount=" + predictions.size() +
                '}';
    }
}
