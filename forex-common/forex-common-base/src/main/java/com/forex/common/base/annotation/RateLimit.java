package com.forex.common.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rate-limit annotation. Throttles request frequency via Redis sliding window.
 * 限流注解。基于Redis滑动窗口控制请求频率。
 * Used by RateLimitAspect via AOP. 由RateLimitAspect通过AOP实现。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * Rate-limit key expression (SpEL supported).
     * 限流key表达式(支持SpEL)。
     */
    String key() default "";

    /**
     * Max allowed requests within the window.
     * 时间窗口内最大请求数。
     */
    int limit() default 100;

    /**
     * Sliding window size in seconds.
     * 滑动窗口大小(秒)。
     */
    int windowSeconds() default 1;

    /**
     * Error message when rate limit exceeded.
     * 触发限流时的错误提示。
     */
    String message() default "请求过于频繁，请稍后再试";
}
