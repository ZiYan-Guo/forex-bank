package com.forex.common.security.aspect;

import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.common.security.annotation.RequireRole;
import com.forex.common.security.util.UserContextHolder;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(RequireRole requireRole) {
        List<String> userRoles = UserContextHolder.getRoles();
        if (userRoles.isEmpty()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        String[] requiredRoles = requireRole.value();
        if (requiredRoles.length == 0) {
            return;
        }
        boolean matched;
        if (requireRole.matchAll()) {
            matched = userRoles.containsAll(Arrays.asList(requiredRoles));
        } else {
            matched = userRoles.stream().anyMatch(r -> Arrays.asList(requiredRoles).contains(r));
        }
        if (!matched) {
            log.warn("角色权限不足: 需要={}, 当前={}", Arrays.toString(requiredRoles), userRoles);
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "角色权限不足");
        }
    }

    @Before("@annotation(requirePermission)")
    public void checkPermission(RequirePermission requirePermission) {
        List<String> userPermissions = UserContextHolder.getPermissions();
        if (userPermissions.isEmpty()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        String[] requiredPermissions = requirePermission.value();
        if (requiredPermissions.length == 0) {
            return;
        }
        boolean matched;
        if (requirePermission.matchAll()) {
            matched = userPermissions.containsAll(Arrays.asList(requiredPermissions));
        } else {
            matched = userPermissions.stream().anyMatch(p -> Arrays.asList(requiredPermissions).contains(p));
        }
        if (!matched) {
            log.warn("操作权限不足: 需要={}, 当前={}", Arrays.toString(requiredPermissions), userPermissions);
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "操作权限不足");
        }
    }
}
