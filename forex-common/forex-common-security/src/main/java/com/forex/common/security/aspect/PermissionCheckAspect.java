package com.forex.common.security.aspect;

import com.forex.common.exception.BusinessException;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.common.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 权限检查切面
 * 自动检查用户是否拥有调用该方法所需的权限
 */
@Aspect
@Component
@Slf4j
public class PermissionCheckAspect {
    
    @Around("@annotation(permission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission permission) throws Throwable {
        String[] requiredPermissions = permission.value();
        RequirePermission.PermissionMode mode = permission.mode();
        
        // 获取当前用户权限
        Set<String> userPermissions = SecurityUtils.getCurrentUserPermissions();
        
        if (userPermissions == null || userPermissions.isEmpty()) {
            throw new BusinessException("E1001", "当前用户无权限信息");
        }
        
        Set<String> required = new HashSet<>(Arrays.asList(requiredPermissions));
        boolean hasPermission;
        
        if (mode == RequirePermission.PermissionMode.ALL) {
            // 所有权限都必须拥有
            hasPermission = userPermissions.containsAll(required);
        } else {
            // 任意一个权限即可
            hasPermission = required.stream().anyMatch(userPermissions::contains);
        }
        
        if (!hasPermission) {
            String currentUser = SecurityUtils.getCurrentUsername();
            log.warn("Permission denied for user [{}], required: {}, mode: {}", 
                    currentUser, Arrays.toString(requiredPermissions), mode);
            throw new BusinessException("E1002", permission.message());
        }
        
        log.debug("Permission check passed for user: {}", SecurityUtils.getCurrentUsername());
        return joinPoint.proceed();
    }
}
