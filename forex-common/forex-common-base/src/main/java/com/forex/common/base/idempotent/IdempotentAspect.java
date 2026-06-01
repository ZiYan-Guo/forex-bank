package com.forex.common.base.idempotent;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import com.forex.common.base.util.SpelUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import java.time.Duration;
@Slf4j
@Aspect
@Component
@ConditionalOnBean(RedissonClient.class)
@RequiredArgsConstructor
public class IdempotentAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String idempotentKey = SpelUtil.resolveTemplate(signature.getMethod(), joinPoint.getArgs(), idempotent.key());
        String redisKey = "idempotent:" + idempotentKey;
        RBucket<String> bucket = redissonClient.getBucket(redisKey);

        boolean set = bucket.setIfAbsent("1", Duration.ofSeconds(idempotent.expireSeconds()));
        if (!set) {
            log.warn("重复请求被拦截: key={}", redisKey);
            throw new BusinessException(ResultCode.IDEMPOTENT_REPEATED.getCode(), idempotent.message());
        }
        log.debug("幂等通过: key={}", redisKey);
        return joinPoint.proceed();
    }
}
