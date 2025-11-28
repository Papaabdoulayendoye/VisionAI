package org.example.visionai.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Requête de prédiction pour un batch d'images.
 */
public final class ImageBatchRequest {

    private final List<ImageRequest> images;

    public ImageBatchRequest(List<ImageRequest> images) {
        this.images = images == null ? List.of() : List.copyOf(images);
    }

    public List<ImageRequest> getImages() {
        return Collections.unmodifiableList(images);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageBatchRequest that = (ImageBatchRequest) o;
        return Objects.equals(images, that.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(images);
    }

    @Override
    public String toString() {
        return "ImageBatchRequest{" +
                "imagesCount=" + images.size() +
                '}';
    }
}
