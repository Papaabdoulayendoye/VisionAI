package org.example.visionai.infrastructure.model;

import io.grpc.ManagedChannel;
import org.example.visionai.application.PythonModelClient;
import org.example.visionai.domain.ImageBatchRequest;
import org.example.visionai.domain.ImageBatchResponse;
import org.example.visionai.domain.ImagePrediction;
import org.example.visionai.domain.ImageRequest;
import org.example.visionai.grpc.VisionModelGrpc;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Client gRPC vers le microservice Python.
 */
public class GrpcPythonModelClient implements PythonModelClient {

    private final VisionModelGrpc.VisionModelBlockingStub stub;

    public GrpcPythonModelClient(ManagedChannel channel) {
        this.stub = VisionModelGrpc.newBlockingStub(channel);
    }

    @Override
    public ImagePrediction predict(ImageRequest request) {
        org.example.visionai.grpc.ImageBatchRequest grpcBatchRequest =
                org.example.visionai.grpc.ImageBatchRequest.newBuilder()
                        .addImages(toGrpcRequest(request))
                        .build();

        org.example.visionai.grpc.ImageBatchResponse grpcBatchResponse = stub.predictBatch(grpcBatchRequest);
        if (grpcBatchResponse.getPredictionsCount() == 0) {
            throw new IllegalStateException("gRPC service returned empty predictions for single request");
        }
        return toDomain(grpcBatchResponse.getPredictions(0));
    }

    @Override
    public ImageBatchResponse predictBatch(ImageBatchRequest request) {
        org.example.visionai.grpc.ImageBatchRequest grpcRequest =
                org.example.visionai.grpc.ImageBatchRequest.newBuilder()
                        .addAllImages(request.getImages().stream()
                                .map(this::toGrpcRequest)
                                .collect(Collectors.toList()))
                        .build();

        org.example.visionai.grpc.ImageBatchResponse grpcResponse = stub.predictBatch(grpcRequest);
        List<ImagePrediction> predictions = grpcResponse.getPredictionsList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new ImageBatchResponse(predictions);
    }

    private org.example.visionai.grpc.ImageRequest toGrpcRequest(ImageRequest request) {
        return org.example.visionai.grpc.ImageRequest.newBuilder()
                .setId(request.getId())
                .setImageBase64(request.getImageBase64())
                .build();
    }

    private ImagePrediction toDomain(org.example.visionai.grpc.ImagePrediction grpcPrediction) {
        Map<String, Double> features = grpcPrediction.getFeaturesMap();
        return new ImagePrediction(grpcPrediction.getId(), grpcPrediction.getLabel(), grpcPrediction.getScore(), features);
    }
}
