package org.example.visionai.infrastructure;

import org.example.visionai.application.PythonModelClient;
import org.example.visionai.application.VisionPipelineService;
import org.example.visionai.infrastructure.model.RestPythonModelClient;
import org.example.visionai.infrastructure.model.StubPythonModelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration Spring principale pour VisionAI.
 */
@Configuration
public class VisionAiConfiguration {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient pythonWebClient(WebClient.Builder builder,
                                     @Value("${visionai.python.rest.base-url:http://localhost:8001}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    public PythonModelClient pythonModelClient(
            @Value("${visionai.python.client-mode:stub}") String clientMode,
            WebClient pythonWebClient) {
        return switch (clientMode.toLowerCase()) {
            case "rest" -> new RestPythonModelClient(pythonWebClient);
            default -> new StubPythonModelClient();
        };
    }

    @Bean
    public VisionPipelineService visionPipelineService(PythonModelClient pythonModelClient) {
        return new VisionPipelineService(pythonModelClient);
    }
}
