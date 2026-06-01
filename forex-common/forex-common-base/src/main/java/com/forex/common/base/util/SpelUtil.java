package com.forex.common.base.util;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SpelUtil {

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final Pattern PLACEHOLDER = Pattern.compile("#\\{(\\w+)}");

    private SpelUtil() {
    }

    public static String resolveTemplate(Method method, Object[] args, String expression) {
        if (expression == null || expression.isEmpty()) {
            return expression;
        }
        if (PLACEHOLDER.matcher(expression).find()) {
            return resolvePlaceholders(method, args, expression);
        }
        return evaluateSpel(method, args, expression);
    }

    private static String resolvePlaceholders(Method method, Object[] args, String template) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder result = new StringBuilder();
        Parameter[] parameters = method.getParameters();
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String replacement = findParamValue(parameters, args, paramName);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String evaluateSpel(Method method, Object[] args, String expression) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length && i < args.length; i++) {
            String name = parameters[i].isNamePresent() ? parameters[i].getName() : "arg" + i;
            context.setVariable(name, args[i]);
        }
        try {
            Object value = PARSER.parseExpression(expression).getValue(context);
            return value != null ? value.toString() : expression;
        } catch (Exception e) {
            return expression;
        }
    }

    private static String findParamValue(Parameter[] parameters, Object[] args, String paramName) {
        for (int i = 0; i < parameters.length && i < args.length; i++) {
            if (paramName.equals(parameters[i].getName()) && args[i] != null) {
                return args[i].toString();
            }
        }
        return "";
    }
}
