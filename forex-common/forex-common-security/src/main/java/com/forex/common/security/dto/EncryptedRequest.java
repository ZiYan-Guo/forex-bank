package com.forex.common.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加密请求 DTO
 * 用于传输加密的请求体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncryptedRequest {
    
    /**
     * 加密后的请求体（Base64 编码）
     */
    @JsonProperty("data")
    private String encryptedData;
    
    /**
     * 加密算法版本
     * v1: AES-256-CBC (SM4)
     * v2: RSA-OAEP + AES
     */
    @JsonProperty("version")
    private String version = "v1";
    
    /**
     * 初始化向量 (IV)，Base64 编码
     * 仅在使用 CBC 模式时需要
     */
    @JsonProperty("iv")
    private String iv;
    
    /**
     * 时间戳（用于防重放攻击）
     */
    @JsonProperty("timestamp")
    private Long timestamp;
    
    /**
     * 签名（HMAC-SHA256）
     */
    @JsonProperty("signature")
    private String signature;
}
