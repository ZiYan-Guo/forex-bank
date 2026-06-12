package com.forex.common.idempotent;

import java.lang.annotation.*;

/**
 * 幂等性保护注解
 * 用于标记需要幂等性保护的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    
    /**
     * 幂等令牌参数名称
     */
    String tokenParamName() default "idempotentToken";
    
    /**
     * 令牌过期时间（秒）
     */
    long expireTime() default 3600;
    
    /**
     * 是否自动删除令牌
     */
    boolean autoDelete() default true;
}
