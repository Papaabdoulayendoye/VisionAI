package org.example.visionai.infrastructure;

import org.example.visionai.application.PythonModelClient;
import org.example.visionai.application.VisionPipelineService;
import org.example.visionai.infrastructure.model.StubPythonModelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring principale pour VisionAI.
 */
@Configuration
public class VisionAiConfiguration {

    @Bean
    public PythonModelClient pythonModelClient() {
        // Implémentation stub, remplacée plus tard par des clients REST/gRPC
        return new StubPythonModelClient();
    }

    @Bean
    public VisionPipelineService visionPipelineService(PythonModelClient pythonModelClient) {
        return new VisionPipelineService(pythonModelClient);
    }
}
