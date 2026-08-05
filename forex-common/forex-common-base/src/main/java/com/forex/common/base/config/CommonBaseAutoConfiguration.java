package com.forex.common.base.config;

import com.forex.common.base.exception.GlobalExceptionHandler;
import com.forex.common.base.idempotent.IdempotencyKeyResolver;
import com.forex.common.base.idempotent.IdempotentAspect;
import com.forex.common.base.lock.RedisLockAspect;
import com.forex.common.base.ratelimit.RateLimitAspect;
import com.forex.common.base.web.RequestTraceFilter;
import com.forex.common.base.web.TraceResponseBodyAdvice;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Common base auto-configuration for shared infrastructure concerns.
 * 公共基础自动配置，用于装配共享基础设施能力。
 *
 * <p>Business services usually scan only their own package, so common AOP components must be
 * imported through Spring Boot auto-configuration. This keeps service startup classes clean and
 * makes idempotency, distributed lock, rate limit, and exception handling consistently available.
 * 业务服务通常只扫描自身包，因此公共AOP组件必须通过Spring Boot自动配置导入。
 * 这样可以保持启动类简洁，并统一启用幂等、分布式锁、限流和异常处理能力。</p>
 */
@AutoConfiguration(afterName = "org.redisson.spring.starter.RedissonAutoConfiguration")
@Import({
        GlobalExceptionHandler.class,
        IdempotencyKeyResolver.class,
        IdempotentAspect.class,
        RedisLockAspect.class,
        RateLimitAspect.class,
        RequestTraceFilter.class,
        TraceResponseBodyAdvice.class
})
public class CommonBaseAutoConfiguration {
}
