package com.forex.ai.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "forex.ai")
public class ModelProviderConfig {

    private String provider;
    private String defaultModel;
    private String embeddingModel;
    private String knowledgeBasePath;

    private Prediction prediction = new Prediction();
    private Risk risk = new Risk();

    @Data
    public static class Prediction {
        private int historyDays = 90;
        private double confidenceLevel = 0.95;
    }

    @Data
    public static class Risk {
        private double amlThreshold = 0.7;
        private double fuzzyMatchThreshold = 0.8;
    }

    public boolean isOllamaProvider() {
        return "ollama".equalsIgnoreCase(provider);
    }

    public boolean isOpenAiProvider() {
        return "openai".equalsIgnoreCase(provider);
    }

    public boolean isDeepSeekProvider() {
        return "deepseek".equalsIgnoreCase(provider);
    }

    public String resolveModel(String preferredModel) {
        if (preferredModel != null && !preferredModel.isBlank()) {
            return preferredModel;
        }
        return defaultModel;
    }
}
