package com.forex.common.security.annotation;

/**
 * 限流类型枚举
 */
public enum RateLimitType {
    
    /**
     * 基于用户限流
     */
    USER,
    
    /**
     * 基于 IP 限流
     */
    IP,
    
    /**
     * 基于 API 端点限流
     */
    API,
    
    /**
     * 基于用户+IP 组合限流
     */
    USER_IP
}
