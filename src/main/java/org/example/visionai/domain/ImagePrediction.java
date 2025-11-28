package org.example.visionai.domain;

import java.util.Map;
import java.util.Objects;

/**
 * Résultat de la prédiction pour une image.
 */
public final class ImagePrediction {

    private final String id;
    private final String label;
    private final double score;
    private final Map<String, Double> features;

    public ImagePrediction(String id, String label, double score, Map<String, Double> features) {
        this.id = id;
        this.label = label;
        this.score = score;
        this.features = features;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public double getScore() {
        return score;
    }

    public Map<String, Double> getFeatures() {
        return features;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImagePrediction that = (ImagePrediction) o;
        return Double.compare(that.score, score) == 0 && Objects.equals(id, that.id) && Objects.equals(label, that.label) && Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, score, features);
    }

    @Override
    public String toString() {
        return "ImagePrediction{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", score=" + score +
                '}';
    }
}
