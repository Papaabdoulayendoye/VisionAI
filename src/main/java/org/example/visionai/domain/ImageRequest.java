package org.example.visionai.domain;

import java.util.Objects;

/**
 * Représente une requête de classification d'image.
 * L'image est encodée en Base64 côté API pour rester indépendante du stockage.
 */
public final class ImageRequest {

    private final String id;
    private final String imageBase64;

    public ImageRequest(String id, String imageBase64) {
        this.id = id;
        this.imageBase64 = imageBase64;
    }

    public String getId() {
        return id;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageRequest that = (ImageRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(imageBase64, that.imageBase64);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageBase64);
    }

    @Override
    public String toString() {
        return "ImageRequest{" +
                "id='" + id + '\'' +
                '}';
    }
}
