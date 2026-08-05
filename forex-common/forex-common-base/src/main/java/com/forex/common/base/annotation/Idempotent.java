package com.forex.common.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Idempotent annotation. Prevents duplicate submissions via a business key and request fingerprint.
 * 幂等注解。通过业务键和请求指纹防止重复提交。
 *
 * <p>The key expression must describe the business scope only. Do not append current time,
 * random values, or UUIDs to the expression, otherwise every retry becomes a new request.
 * key表达式只描述业务范围，禁止拼接当前时间、随机数或UUID，否则重试请求会被当成新请求。</p>
 *
 * <p>{@link com.forex.common.base.idempotent.IdempotentAspect} combines the expression with the
 * Idempotency-Key request header. When the header is absent, it derives a deterministic
 * fingerprint from the method arguments.
 * {@link com.forex.common.base.idempotent.IdempotentAspect} 会将表达式与 Idempotency-Key 请求头组合；
 * 未提供请求头时，则根据方法参数生成稳定指纹。</p>
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
