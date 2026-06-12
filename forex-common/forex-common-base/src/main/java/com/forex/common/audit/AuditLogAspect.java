package com.forex.common.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forex.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 审计日志切面
 * 自动记录标记为 @AuditLog 的方法调用
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogAspect {
    
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;
    
    @Around("@annotation(auditLog)")
    public Object auditLogging(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            recordAuditLog(joinPoint, auditLog, result, exception, duration);
        }
    }
    
    private void recordAuditLog(ProceedingJoinPoint joinPoint, 
                                AuditLog auditLog, 
                                Object result, 
                                Throwable exception, 
                                long duration) {
        try {
            AuditLogDto logDto = new AuditLogDto();
            logDto.setUserId(SecurityUtils.getCurrentUserId());
            logDto.setUsername(SecurityUtils.getCurrentUsername());
            logDto.setOperationType(auditLog.operation());
            logDto.setEntityType(auditLog.entity());
            logDto.setMethodName(joinPoint.getSignature().getName());
            logDto.setClassName(joinPoint.getTarget().getClass().getName());
            
            if (auditLog.recordArgs()) {
                logDto.setArgs(serializeArgs(joinPoint.getArgs()));
            }
            
            if (auditLog.recordResult() && result != null) {
                logDto.setResult(objectMapper.writeValueAsString(result));
            }
            
            logDto.setIpAddress(getClientIp());
            logDto.setDuration(duration);
            logDto.setStatus(exception == null ? "SUCCESS" : "FAILED");
            logDto.setErrorMessage(exception != null ? exception.getMessage() : null);
            logDto.setExecutedAt(LocalDateTime.now());
            
            auditLogService.saveAsync(logDto);
        } catch (Exception e) {
            log.error("Failed to record audit log", e);
        }
    }
    
    private String serializeArgs(Object[] args) {
        try {
            List<String> serialized = new ArrayList<>();
            for (Object arg : args) {
                if (arg == null) {
                    serialized.add("null");
                } else if (isPrimitive(arg)) {
                    serialized.add(arg.toString());
                } else {
                    serialized.add(objectMapper.writeValueAsString(arg));
                }
            }
            return "[" + String.join(",", serialized) + "]";
        } catch (Exception e) {
            return "[error serializing args]";
        }
    }
    
    private boolean isPrimitive(Object obj) {
        return obj instanceof String || obj instanceof Number || 
               obj instanceof Boolean || obj instanceof Character;
    }
    
    private String getClientIp() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
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
        return ip;
    }
}
