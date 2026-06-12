package com.forex.common.security.annotation;

import java.lang.annotation.*;

/**
 * 权限检查注解
 * 用于检查用户是否拥有特定权限
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 权限编码
     * 例如: "exchange:create", "payment:approve"
     */
    String[] value();
    
    /**
     * 权限验证方式
     * ANY: 任意一个权限即可（或关系）
     * ALL: 必须所有权限（与关系）
     */
    PermissionMode mode() default PermissionMode.ANY;
    
    /**
     * 权限验证失败时的错误消息
     */
    String message() default "当前用户无该操作权限";
    
    enum PermissionMode {
        ANY,  // 任意一个
        ALL   // 全部
    }
}
