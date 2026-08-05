package com.forex.common.base.idempotent;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.constant.CacheConstants;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Slf4j
@Aspect
@Component
@Order(1)
@ConditionalOnBean(RedissonClient.class)
@RequiredArgsConstructor
public class IdempotentAspect {

    private final RedissonClient redissonClient;
    private final IdempotencyKeyResolver idempotencyKeyResolver;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String idempotentKey = idempotencyKeyResolver.resolve(
                signature.getMethod(), joinPoint.getArgs(), idempotent.key());
        String redisKey = CacheConstants.IDEMPOTENT_PREFIX + idempotentKey;
        RBucket<String> bucket = redissonClient.getBucket(redisKey);

        boolean set = bucket.setIfAbsent("1", Duration.ofSeconds(idempotent.expireSeconds()));
        if (!set) {
            log.warn(
                    "Duplicate request intercepted / 重复请求被拦截, method={}, keyDigest={}",
                    signature.toShortString(),
                    idempotencyKeyResolver.digest(redisKey));
            throw new BusinessException(ResultCode.IDEMPOTENT_REPEATED.getCode(), idempotent.message());
        }
        log.debug(
                "Idempotency check passed / 幂等校验通过, method={}, keyDigest={}, expireSeconds={}",
                signature.toShortString(),
                idempotencyKeyResolver.digest(redisKey),
                idempotent.expireSeconds());
        Object result = joinPoint.proceed();
        log.debug(
                "Idempotent request completed / 幂等请求完成, method={}, keyDigest={}",
                signature.toShortString(),
                idempotencyKeyResolver.digest(redisKey));
        return result;
    }
}
