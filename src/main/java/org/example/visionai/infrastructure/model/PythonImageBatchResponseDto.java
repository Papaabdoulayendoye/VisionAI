package org.example.visionai.infrastructure.model;

import java.util.List;

/**
 * Représente la réponse batch retournée par le service Python (REST).
 */
public class PythonImageBatchResponseDto {

    private List<PythonImagePredictionDto> predictions;

    public PythonImageBatchResponseDto() {
        // Default constructor for JSON binding
    }

    public List<PythonImagePredictionDto> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<PythonImagePredictionDto> predictions) {
        this.predictions = predictions;
    }
}
