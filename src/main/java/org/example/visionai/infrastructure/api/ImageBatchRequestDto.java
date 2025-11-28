package org.example.visionai.infrastructure.api;

import java.util.List;

/**
 * DTO pour les requêtes de prédiction par batch.
 */
public class ImageBatchRequestDto {

    private List<ImageRequestDto> images;

    public ImageBatchRequestDto() {
        // Constructeur par défaut requis pour la désérialisation JSON
    }

    public List<ImageRequestDto> getImages() {
        return images;
    }

    public void setImages(List<ImageRequestDto> images) {
        this.images = images;
    }
}
