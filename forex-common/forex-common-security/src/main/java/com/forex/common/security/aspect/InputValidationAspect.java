package com.forex.common.security.aspect;

import com.forex.common.exception.BusinessException;
import com.forex.common.security.annotation.ValidateInput;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

/**
 * 输入验证切面
 * 自动验证标记为 @ValidateInput 的参数
 */
@Aspect
@Component
@Slf4j
public class InputValidationAspect {
    
    @Around("execution(* *(..)) && (@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping))")
    public Object validateInput(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            
            ValidateInput annotation = parameter.getAnnotation(ValidateInput.class);
            if (annotation != null) {
                validateParameter(arg, annotation, parameter.getName());
            }
        }
        
        return joinPoint.proceed();
    }
    
    /**
     * 验证单个参数
     */
    private void validateParameter(Object value, ValidateInput validation, String paramName) {
        // 检查 null 值
        if (value == null) {
            if (!validation.allowNull()) {
                throw new BusinessException("E1004", 
                    String.format("参数 %s 不能为空", paramName));
            }
            return;
        }
        
        if (value instanceof String) {
            String strValue = (String) value;
            
            // 检查空字符串
            if (strValue.isEmpty() && !validation.allowBlank()) {
                throw new BusinessException("E1005", 
                    String.format("参数 %s 不能为空字符串", paramName));
            }
            
            // 检查长度
            if (strValue.length() > validation.maxLength()) {
                throw new BusinessException("E1006", 
                    String.format("参数 %s 长度不能超过 %d", paramName, validation.maxLength()));
            }
            
            if (strValue.length() < validation.minLength()) {
                throw new BusinessException("E1007", 
                    String.format("参数 %s 长度不能少于 %d", paramName, validation.minLength()));
            }
            
            // 检查正则表达式
            if (!validation.pattern().isEmpty()) {
                Pattern pattern = Pattern.compile(validation.pattern());
                if (!pattern.matcher(strValue).matches()) {
                    throw new BusinessException("E1008", validation.message());
                }
            }
        }
    }
    
    /**
     * 从 joinPoint 获取目标方法
     */
    private Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        Object target = joinPoint.getTarget();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i] == null ? Object.class : args[i].getClass();
        }
        
        return target.getClass().getMethod(methodName, argTypes);
    }
}
