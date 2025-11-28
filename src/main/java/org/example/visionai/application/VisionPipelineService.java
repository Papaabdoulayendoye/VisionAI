package org.example.visionai.application;

import org.example.visionai.domain.ImageBatchRequest;
import org.example.visionai.domain.ImageBatchResponse;
import org.example.visionai.domain.ImagePrediction;
import org.example.visionai.domain.ImageRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service d'orchestration du pipeline VisionAI côté Java.
 * Gère le cache des prédictions et le traitement par batch.
 */
public class VisionPipelineService {

    private final PythonModelClient pythonModelClient;
    private final Map<String, ImagePrediction> cache = new ConcurrentHashMap<>();

    public VisionPipelineService(PythonModelClient pythonModelClient) {
        this.pythonModelClient = Objects.requireNonNull(pythonModelClient, "pythonModelClient must not be null");
    }

    public ImagePrediction predict(ImageRequest request) {
        String key = hashImage(request.getImageBase64());
        ImagePrediction cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        ImagePrediction prediction = pythonModelClient.predict(request);
        cache.put(key, prediction);
        return prediction;
    }

    public ImageBatchResponse predictBatch(ImageBatchRequest request) {
        // Préparer les résultats dans le même ordre que les requêtes
        List<ImageRequest> inputs = request.getImages();
        List<ImagePrediction> finalPredictions = new ArrayList<>(inputs.size());

        // Map index -> request pour celles qui doivent être envoyées au modèle Python
        Map<Integer, ImageRequest> toPredict = new LinkedHashMap<>();

        for (int i = 0; i < inputs.size(); i++) {
            ImageRequest imgReq = inputs.get(i);
            String key = hashImage(imgReq.getImageBase64());
            ImagePrediction cached = cache.get(key);
            if (cached != null) {
                finalPredictions.add(cached);
            } else {
                // Placeholder, sera remplacé après appel au modèle Python
                finalPredictions.add(null);
                toPredict.put(i, imgReq);
            }
        }

        if (!toPredict.isEmpty()) {
            // Appel batch au modèle Python uniquement pour les images non en cache
            List<ImageRequest> toPredictList = new ArrayList<>(toPredict.values());
            ImageBatchResponse responseFromModel = pythonModelClient.predictBatch(new ImageBatchRequest(toPredictList));
            List<ImagePrediction> newPredictions = responseFromModel.getPredictions();

            int idx = 0;
            for (Map.Entry<Integer, ImageRequest> entry : toPredict.entrySet()) {
                int originalIndex = entry.getKey();
                ImagePrediction prediction = newPredictions.get(idx++);

                String key = hashImage(entry.getValue().getImageBase64());
                cache.put(key, prediction);
                finalPredictions.set(originalIndex, prediction);
            }
        }

        return new ImageBatchResponse(finalPredictions);
    }

    public ImageBatchResponse predictBatch(List<ImageRequest> requests) {
        return predictBatch(new ImageBatchRequest(requests));
    }

    private String hashImage(String imageBase64) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(imageBase64.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
