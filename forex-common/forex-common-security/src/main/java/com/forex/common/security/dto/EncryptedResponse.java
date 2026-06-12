package com.forex.common.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加密响应 DTO
 * 用于返回加密的响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncryptedResponse {
    
    /**
     * 加密后的响应体（Base64 编码）
     */
    @JsonProperty("data")
    private String encryptedData;
    
    /**
     * 加密算法版本
     */
    @JsonProperty("version")
    private String version = "v1";
    
    /**
     * 初始化向量 (IV)，Base64 编码
     */
    @JsonProperty("iv")
    private String iv;
    
    /**
     * 时间戳
     */
    @JsonProperty("timestamp")
    private Long timestamp;
    
    /**
     * 签名（HMAC-SHA256）
     */
    @JsonProperty("signature")
    private String signature;
    
    /**
     * 响应状态码
     */
    @JsonProperty("code")
    private Integer code;
    
    /**
     * 响应消息
     */
    @JsonProperty("message")
    private String message;
}
