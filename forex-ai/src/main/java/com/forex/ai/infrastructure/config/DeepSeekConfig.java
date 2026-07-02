package com.forex.ai.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "forex.ai.deepseek")
public class DeepSeekConfig {

    private boolean enabled = false;
    private String apiKey;
    private String baseUrl = "https://api.deepseek.com/v1";
    private String chatModel = "deepseek-v4-flash";
    private String embeddingModel = "text-embedding-ada-002";
    private double temperature = 0.7;
    private int maxTokens = 4096;
    private int timeout = 60;
    private int maxRetries = 3;
}
