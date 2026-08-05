package com.forex.common.base.ratelimit;

import com.forex.common.base.annotation.RateLimit;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import com.forex.common.base.util.SpelUtil;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(0)
@Component
@ConditionalOnBean(RedissonClient.class)
public class RateLimitAspect {

    private final RedissonClient redissonClient;

    public RateLimitAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String baseKey = SpelUtil.resolveTemplate(signature.getMethod(), joinPoint.getArgs(), rateLimit.key());
        if (baseKey == null || baseKey.isEmpty()) {
            baseKey = signature.getMethod().getDeclaringClass().getName() + "." + signature.getMethod().getName();
        }
        String limiterKey = "rateLimit:" + baseKey;
        RRateLimiter limiter = redissonClient.getRateLimiter(limiterKey);
        limiter.trySetRate(RateType.OVERALL, rateLimit.limit(), rateLimit.windowSeconds(), RateIntervalUnit.SECONDS);

        if (!limiter.tryAcquire()) {
            log.warn(
                    "Rate limit triggered / 触发限流, method={}, keyDigest={}",
                    signature.toShortString(),
                    Integer.toHexString(limiterKey.hashCode()));
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS.getCode(), rateLimit.message());
        }
        return joinPoint.proceed();
    }
}
