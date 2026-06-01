package com.forex.common.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Redis distributed lock annotation. Applied on methods that need exclusive access.
 * Redis分布式锁注解。用于需要排他访问的方法。
 * Used by RedisLockAspect via AOP. 由RedisLockAspect通过AOP实现。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

    /**
     * Lock key expression (SpEL supported).
     * 锁key表达式(支持SpEL)。
     */
    String key();

    /**
     * Lock auto-release time in seconds.
     * 锁自动释放时间(秒)。
     */
    long leaseTime() default 10;

    /**
     * Max time to wait for lock acquisition.
     * 获取锁最大等待时间。
     */
    long waitTime() default 3;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
