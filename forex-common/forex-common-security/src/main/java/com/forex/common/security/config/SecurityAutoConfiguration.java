package com.forex.common.security.config;

import com.forex.common.security.jwt.JwtUtil;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Security auto-configuration. Creates JwtUtil bean and validates secret key strength at startup.
 * 安全自动配置。创建JwtUtil并在启动时校验密钥强度。
 */
@Slf4j
@Data
@Configuration
@ConditionalOnProperty(prefix = "forex.security", name = "jwt-secret")
@ConfigurationProperties(prefix = "forex.security")
public class SecurityAutoConfiguration {

    private static final int MIN_KEY_BYTES = 32;

    private String jwtSecret;
    private boolean sm2Enabled = false;
    private String sm2PrivateKey;
    private String sm2PublicKey;
    private String sm4Key;

    /**
     * Validate JWT secret is at least 32 characters (256 bits). Throws IllegalStateException if too weak.
     * 启动时校验JWT密钥至少32字符。
     */
    @PostConstruct
    public void validate() {
        if (jwtSecret == null || jwtSecret.length() < MIN_KEY_BYTES) {
            throw new IllegalStateException(
                    "JWT secret must be at least " + MIN_KEY_BYTES + " characters (256 bits). " +
                    "Set 'forex.security.jwt-secret' in configuration. Current length: " +
                    (jwtSecret == null ? 0 : jwtSecret.length()));
        }
        log.info("JWT secret validated / JWT密钥校验通过, length={}", jwtSecret.length());
    }

    /**
     * Create JwtUtil bean.
     * 创建JwtUtil Bean。
     */
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecret);
    }

    @Bean
    @ConditionalOnProperty(name = "forex.security.sm2-enabled", havingValue = "true")
    public com.forex.common.security.sm.Sm2Util sm2Util() {
        return new com.forex.common.security.sm.Sm2Util(sm2PrivateKey, sm2PublicKey);
    }
}
