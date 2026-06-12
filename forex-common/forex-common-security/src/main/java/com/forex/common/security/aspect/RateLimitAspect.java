package com.forex.common.security.aspect;

import com.forex.common.exception.BusinessException;
import com.forex.common.security.annotation.RateLimit;
import com.forex.common.security.annotation.RateLimitType;
import com.forex.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 * 基于 Redis 实现分布式限流
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {
    
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";
    
    private final RedissonClient redissonClient;
    
    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = generateKey(rateLimit.type());
        
        if (!checkRateLimit(key, rateLimit.maxRequests(), rateLimit.windowSeconds())) {
            log.warn("Rate limit exceeded for key: {}, max requests: {}, window: {}s", 
                    key, rateLimit.maxRequests(), rateLimit.windowSeconds());
            throw new BusinessException("E1003", rateLimit.message());
        }
        
        return joinPoint.proceed();
    }
    
    /**
     * 检查是否超过限流阈值
     */
    private boolean checkRateLimit(String key, int maxRequests, int windowSeconds) {
        String redisKey = RATE_LIMIT_KEY_PREFIX + key;
        RAtomicLong atomicLong = redissonClient.getAtomicLong(redisKey);
        
        long current = atomicLong.incrementAndGet();
        
        // 首次访问，设置过期时间
        if (current == 1) {
            atomicLong.expire(windowSeconds, TimeUnit.SECONDS);
        }
        
        return current <= maxRequests;
    }
    
    /**
     * 根据限流类型生成 key
     */
    private String generateKey(RateLimitType type) {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        StringBuilder key = new StringBuilder();
        key.append(type.name()).append(":");
        
        switch (type) {
            case USER:
                key.append(SecurityUtils.getCurrentUserId());
                break;
            case IP:
                key.append(getClientIp(attributes));
                break;
            case API:
                HttpServletRequest request = attributes.getRequest();
                key.append(request.getMethod()).append(":").append(request.getRequestURI());
                break;
            case USER_IP:
                key.append(SecurityUtils.getCurrentUserId()).append(":").append(getClientIp(attributes));
                break;
            default:
                key.append("unknown");
        }
        
        return key.toString();
    }
    
    /**
     * 获取客户端 IP
     */
    private String getClientIp(ServletRequestAttributes attributes) {
        if (attributes == null) {
            return "UNKNOWN";
        }
        
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        
        // 处理多个 IP（取第一个）
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
