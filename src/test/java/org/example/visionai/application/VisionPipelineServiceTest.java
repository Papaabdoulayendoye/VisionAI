package org.example.visionai.application;

import org.example.visionai.domain.ImageBatchRequest;
import org.example.visionai.domain.ImageBatchResponse;
import org.example.visionai.domain.ImagePrediction;
import org.example.visionai.domain.ImageRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VisionPipelineServiceTest {

    private static class CountingPythonModelClient implements PythonModelClient {

        int singleCalls = 0;
        int batchCalls = 0;

        @Override
        public ImagePrediction predict(ImageRequest request) {
            singleCalls++;
            return new ImagePrediction(request.getId(), "LABEL", 1.0, Map.of());
        }

        @Override
        public ImageBatchResponse predictBatch(ImageBatchRequest request) {
            batchCalls++;
            List<ImagePrediction> predictions = request.getImages().stream()
                    .map(img -> new ImagePrediction(img.getId(), "LABEL", 1.0, Map.of()))
                    .toList();
            return new ImageBatchResponse(predictions);
        }
    }

    @Test
    void predict_usesCacheForSameImage() {
        CountingPythonModelClient client = new CountingPythonModelClient();
        VisionPipelineService service = new VisionPipelineService(client);

        ImageRequest req = new ImageRequest("1", "BASE64_IMAGE");

        ImagePrediction p1 = service.predict(req);
        ImagePrediction p2 = service.predict(req);

        assertThat(p1.getId()).isEqualTo("1");
        assertThat(p2.getId()).isEqualTo("1");
        assertThat(client.singleCalls).isEqualTo(1);
    }

    @Test
    void predictBatch_callsPythonOnlyForUncachedImages() {
        CountingPythonModelClient client = new CountingPythonModelClient();
        VisionPipelineService service = new VisionPipelineService(client);

        ImageRequest img1 = new ImageRequest("1", "IMG_A");
        ImageRequest img2 = new ImageRequest("2", "IMG_B");

        // Première fois : les deux images passent par le modèle Python
        ImageBatchResponse first = service.predictBatch(List.of(img1, img2));
        assertThat(first.getPredictions()).hasSize(2);
        assertThat(client.batchCalls).isEqualTo(1);

        // Deuxième fois avec les mêmes images : tout doit venir du cache
        ImageBatchResponse second = service.predictBatch(List.of(img1, img2));
        assertThat(second.getPredictions()).hasSize(2);
        assertThat(client.batchCalls).isEqualTo(1);
    }
}
