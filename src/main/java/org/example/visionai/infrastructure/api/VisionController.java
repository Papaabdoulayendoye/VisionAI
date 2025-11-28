package org.example.visionai.infrastructure.api;

import org.example.visionai.application.VisionPipelineService;
import org.example.visionai.domain.ImageBatchRequest;
import org.example.visionai.domain.ImageBatchResponse;
import org.example.visionai.domain.ImagePrediction;
import org.example.visionai.domain.ImageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST principal de VisionAI pour la prédiction d'images.
 */
@RestController
@RequestMapping(path = "/api/v1/vision", produces = MediaType.APPLICATION_JSON_VALUE)
public class VisionController {

    private final VisionPipelineService visionPipelineService;

    public VisionController(VisionPipelineService visionPipelineService) {
        this.visionPipelineService = visionPipelineService;
    }

    @PostMapping(path = "/predict", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ImagePrediction predict(@RequestBody ImageRequestDto requestDto) {
        ImageRequest request = new ImageRequest(requestDto.getId(), requestDto.getImageBase64());
        return visionPipelineService.predict(request);
    }

    @PostMapping(path = "/predict-batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ImageBatchResponse predictBatch(@RequestBody ImageBatchRequestDto batchRequestDto) {
        List<ImageRequest> requests = batchRequestDto.getImages().stream()
                .map(dto -> new ImageRequest(dto.getId(), dto.getImageBase64()))
                .collect(Collectors.toList());
        return visionPipelineService.predictBatch(new ImageBatchRequest(requests));
    }
}
