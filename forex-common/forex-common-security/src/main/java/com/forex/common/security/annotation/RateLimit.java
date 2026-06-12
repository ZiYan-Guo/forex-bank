package com.forex.common.security.annotation;

import java.lang.annotation.*;

/**
 * 限流注解
 * 用于限制 API 调用频率，防止滥用和 DDoS 攻击
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * 限流类型
     */
    RateLimitType type() default RateLimitType.USER;
    
    /**
     * 时间窗口（秒）
     * 默认 1 分钟
     */
    int windowSeconds() default 60;
    
    /**
     * 时间窗口内允许的最大请求数
     */
    int maxRequests() default 100;
    
    /**
     * 限流失败时的错误消息
     */
    String message() default "请求过于频繁，请稍后再试";
    
    /**
     * 是否记录限流事件到审计日志
     */
    boolean recordEvent() default true;
}
