package com.forex.common.audit;

import java.lang.annotation.*;

/**
 * 审计日志注解
 * 用于标记需要记录审计日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    
    /**
     * 操作类型 (CREATE/UPDATE/DELETE/EXPORT/APPROVE)
     */
    String operation();
    
    /**
     * 实体类型 (ExchangeOrder/FxTrade 等)
     */
    String entity() default "";
    
    /**
     * 是否记录参数 (false 表示敏感数据不记录)
     */
    boolean recordArgs() default true;
    
    /**
     * 是否记录结果 (false 表示大数据不记录)
     */
    boolean recordResult() default true;
}
