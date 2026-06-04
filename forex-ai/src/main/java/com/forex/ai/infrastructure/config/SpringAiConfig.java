package com.forex.ai.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring AI configuration. Creates simple RestTemplate-based AI client.
 * Fallback when Spring AI auto-configuration is not available.
 * Spring AI 配置。当自动配置不可用时回退到 RestTemplate。
 */
@Slf4j
@Configuration
public class SpringAiConfig {

    @Bean
    public RestTemplate aiRestTemplate() {
        log.info("Initializing AI RestTemplate client");
        return new RestTemplate();
    }
}
