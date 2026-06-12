package com.forex.common.security.annotation;

import java.lang.annotation.*;

/**
 * 输入验证注解
 * 用于标记需要进行输入验证的参数
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateInput {
    
    /**
     * 是否允许 null 值
     */
    boolean allowNull() default false;
    
    /**
     * 是否允许空字符串
     */
    boolean allowBlank() default false;
    
    /**
     * 最大长度（仅对字符串有效）
     */
    int maxLength() default 10000;
    
    /**
     * 最小长度（仅对字符串有效）
     */
    int minLength() default 0;
    
    /**
     * 正则表达式校验（可选）
     */
    String pattern() default "";
    
    /**
     * 验证失败时的错误消息
     */
    String message() default "输入参数非法";
}
