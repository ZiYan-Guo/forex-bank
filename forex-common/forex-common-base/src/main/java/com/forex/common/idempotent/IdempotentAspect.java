package com.forex.common.idempotent;

import com.forex.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性保护切面
 * 防止重复提交和并发冲突
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class IdempotentAspect {
    
    private static final String IDEMPOTENT_KEY_PREFIX = "idempotent:";
    private static final String IDEMPOTENT_PROCESSING = "processing";
    private static final String IDEMPOTENT_RESULT = "result:";
    
    private final RedissonClient redissonClient;
    
    @Around("@annotation(idempotent)")
    public Object idempotentCheck(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String token = getIdempotentToken(idempotent.tokenParamName());
        
        if (token == null || token.isEmpty()) {
            throw new BusinessException("E001", "幂等令牌不能为空");
        }
        
        String lockKey = IDEMPOTENT_KEY_PREFIX + token;
        RBucket<String> bucket = redissonClient.getBucket(lockKey);
        
        // 检查是否已处理
        if (bucket.isExists()) {
            String value = bucket.get();
            if (IDEMPOTENT_PROCESSING.equals(value)) {
                throw new BusinessException("E002", "请求正在处理中，请勿重复提交");
            }
            if (value != null && value.startsWith(IDEMPOTENT_RESULT)) {
                // 返回缓存的结果
                log.info("Returning cached result for idempotent token: {}", token);
                return value.substring(IDEMPOTENT_RESULT.length());
            }
        }
        
        // 标记为处理中
        bucket.set(IDEMPOTENT_PROCESSING, idempotent.expireTime(), TimeUnit.SECONDS);
        
        try {
            Object result = joinPoint.proceed();
            
            // 缓存结果
            if (result != null) {
                bucket.set(IDEMPOTENT_RESULT + result.toString(), 
                          idempotent.expireTime(), TimeUnit.SECONDS);
            } else {
                bucket.delete();
            }
            
            if (idempotent.autoDelete()) {
                bucket.deleteAsync();
            }
            
            return result;
        } catch (Throwable e) {
            bucket.delete();
            throw e;
        }
    }
    
    private String getIdempotentToken(String paramName) {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 优先从请求头获取
        String token = request.getHeader(paramName);
        if (token == null || token.isEmpty()) {
            // 其次从请求参数获取
            token = request.getParameter(paramName);
        }
        
        return token;
    }
}
