package com.forex.common.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 安全响应头过滤器
 * 添加必要的安全响应头防止常见攻击
 */
@Component
@Slf4j
public class SecurityHeaderFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 防止浏览器 MIME 类型嗅探
            response.setHeader("X-Content-Type-Options", "nosniff");
            
            // 防止点击劫持（不允许在 iframe 中显示）
            response.setHeader("X-Frame-Options", "DENY");
            
            // 启用浏览器 XSS 保护
            response.setHeader("X-XSS-Protection", "1; mode=block");
            
            // 严格传输安全（HSTS）- 强制使用 HTTPS
            response.setHeader("Strict-Transport-Security", 
                            "max-age=31536000; includeSubDomains; preload");
            
            // 内容安全策略 (CSP)
            response.setHeader("Content-Security-Policy", 
                            "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data: https:; " +
                            "font-src 'self'; " +
                            "connect-src 'self'; " +
                            "frame-ancestors 'none'");
            
            // 防止浏览器缓存敏感信息
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 移除服务器信息披露
            response.setHeader("Server", "Apache");
            response.removeHeader("X-Powered-By");
            
            // 权限策略（特性策略）
            response.setHeader("Permissions-Policy", 
                            "geolocation=(), " +
                            "camera=(), " +
                            "microphone=(), " +
                            "payment=()");
            
            // 参考政策
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in SecurityHeaderFilter", e);
            filterChain.doFilter(request, response);
        }
    }
}
