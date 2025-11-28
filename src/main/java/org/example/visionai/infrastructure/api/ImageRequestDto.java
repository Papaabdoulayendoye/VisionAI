package org.example.visionai.infrastructure.api;

/**
 * DTO utilisé par l'API REST pour représenter une image à classifier.
 * Contient l'identifiant de l'image et son contenu encodé en Base64.
 */
public class ImageRequestDto {

    private String id;
    private String imageBase64;

    public ImageRequestDto() {
        // Constructeur par défaut requis pour la désérialisation JSON
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
