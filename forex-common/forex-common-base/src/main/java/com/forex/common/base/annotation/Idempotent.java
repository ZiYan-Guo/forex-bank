package com.forex.common.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Idempotent annotation. Prevents duplicate submissions via Redis-based token mechanism.
 * 幂等注解。基于Redis令牌机制防止重复提交。
 * Used by IdempotentAspect via AOP. 由IdempotentAspect通过AOP实现。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * Idempotent key expression (SpEL supported).
     * 幂等key表达式(支持SpEL)。
     */
    String key();

    /**
     * Token expiry time in seconds.
     * 令牌过期时间(秒)。
     */
    long expireSeconds() default 30;

    /**
     * Error message when duplicate submission detected.
     * 检测到重复提交时的错误提示。
     */
    String message() default "请勿重复提交";
}
