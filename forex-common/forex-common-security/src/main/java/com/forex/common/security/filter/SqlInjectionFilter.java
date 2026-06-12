package com.forex.common.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * SQL 注入防护过滤器
 * 检测和阻止潜在的 SQL 注入攻击
 */
@Component
@Slf4j
public class SqlInjectionFilter extends OncePerRequestFilter {
    
    // 常见的 SQL 注入模式
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|" +
        "script|javascript|onerror|onclick|onload|eval|expression|vbscript|" +
        "--|\\/\\*|\\*\\/|xp_|sp_|'; drop|or '1'='1)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 检查查询参数
        String queryString = request.getQueryString();
        if (queryString != null && isSuspicious(queryString)) {
            log.warn("Potential SQL injection detected in query string: {} from IP: {}", 
                    queryString, getClientIp(request));
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 检查是否包含可疑的 SQL 注入模式
     */
    private boolean isSuspicious(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        
        // 检查是否匹配 SQL 注入模式
        return SQL_INJECTION_PATTERN.matcher(value).find();
    }
    
    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
