package org.example.visionai.infrastructure.model;

import java.util.Map;

/**
 * Représente la prédiction retournée par le service Python (REST).
 */
public class PythonImagePredictionDto {

    private String id;
    private String label;
    private double score;
    private Map<String, Double> features;

    public PythonImagePredictionDto() {
        // Default constructor for JSON binding
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Map<String, Double> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Double> features) {
        this.features = features;
    }
}
