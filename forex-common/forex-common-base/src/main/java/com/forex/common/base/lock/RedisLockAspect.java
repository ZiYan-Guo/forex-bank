package com.forex.common.base.lock;

import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import com.forex.common.base.util.SpelUtil;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@Order(2)
@ConditionalOnBean(RedissonClient.class)
public class RedisLockAspect {

    private final RedissonClient redissonClient;

    public RedisLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String lockKey = SpelUtil.resolveTemplate(signature.getMethod(), joinPoint.getArgs(), redisLock.key());
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;
        try {
            acquired = lock.tryLock(redisLock.waitTime(), redisLock.leaseTime(), redisLock.timeUnit());
            if (!acquired) {
                log.warn(
                        "Distributed lock acquisition failed / 获取分布式锁失败, method={}, keyDigest={}",
                        signature.toShortString(),
                        keyDigest(lockKey));
                throw new BusinessException(ResultCode.LOCK_FAILED);
            }
            log.debug(
                    "Distributed lock acquired / 获取分布式锁成功, method={}, keyDigest={}",
                    signature.toShortString(),
                    keyDigest(lockKey));
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn(
                    "Distributed lock interrupted / 获取分布式锁被中断, method={}",
                    signature.toShortString());
            throw new BusinessException(ResultCode.LOCK_FAILED);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug(
                        "Distributed lock released / 释放分布式锁成功, method={}, keyDigest={}",
                        signature.toShortString(),
                        keyDigest(lockKey));
            }
        }
    }

    private String keyDigest(String lockKey) {
        return Integer.toHexString(lockKey.hashCode());
    }
}
