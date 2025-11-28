package org.example.visionai.infrastructure.model;

import org.example.visionai.application.PythonModelClient;
import org.example.visionai.domain.ImageBatchRequest;
import org.example.visionai.domain.ImageBatchResponse;
import org.example.visionai.domain.ImagePrediction;
import org.example.visionai.domain.ImageRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Client REST vers le microservice Python.
 */
public class RestPythonModelClient implements PythonModelClient {

    private final WebClient webClient;

    public RestPythonModelClient(WebClient webClient) {
        this.webClient = Objects.requireNonNull(webClient, "webClient must not be null");
    }

    @Override
    public ImagePrediction predict(ImageRequest request) {
        PythonImagePredictionDto dto = webClient.post()
                .uri("/predict")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PythonImagePredictionDto.class)
                .block();

        if (dto == null) {
            throw new IllegalStateException("Python service returned null prediction");
        }

        return toDomain(dto);
    }

    @Override
    public ImageBatchResponse predictBatch(ImageBatchRequest request) {
        PythonImageBatchResponseDto dto = webClient.post()
                .uri("/predict-batch")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PythonImageBatchResponseDto.class)
                .block();

        if (dto == null || dto.getPredictions() == null) {
            throw new IllegalStateException("Python service returned null batch prediction");
        }

        List<ImagePrediction> predictions = dto.getPredictions().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new ImageBatchResponse(predictions);
    }

    private ImagePrediction toDomain(PythonImagePredictionDto dto) {
        Map<String, Double> features = dto.getFeatures();
        return new ImagePrediction(dto.getId(), dto.getLabel(), dto.getScore(), features);
    }
}
