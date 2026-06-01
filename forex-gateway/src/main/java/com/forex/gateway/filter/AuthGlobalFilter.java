package com.forex.gateway.filter;

import com.forex.common.security.jwt.JwtUtil;

import io.jsonwebtoken.Claims;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway authentication global filter. Validates JWT token, extracts user info into headers.
 * 网关全局认证过滤器。验证JWT令牌并提取用户信息到请求头。
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "forex.gateway")
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Name";
    private static final String USER_ROLES_HEADER = "X-User-Roles";
    private static final String USER_PERMISSIONS_HEADER = "X-User-Permissions";

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private List<String> ignoreAuthUrls = new ArrayList<>();

    public AuthGlobalFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public void setIgnoreAuthUrls(List<String> ignoreAuthUrls) {
        this.ignoreAuthUrls = ignoreAuthUrls;
    }

    /**
     * Entry point - checks ignore list, extracts Bearer token, validates JWT, propagates user headers.
     * 入口方法，检查白名单，提取token，校验JWT，转发用户信息。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isIgnoreUrl(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);
        if (token == null) {
            log.warn("未提供认证Token: path={}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = jwtUtil.getUserId(claims);
            String username = jwtUtil.getUsername(claims);
            List<String> roles = jwtUtil.getRoles(claims);
            List<String> permissions = jwtUtil.getPermissions(claims);

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(USER_ID_HEADER, String.valueOf(userId))
                    .header(USER_NAME_HEADER, username)
                    .header(USER_ROLES_HEADER, String.join(",", roles))
                    .header(USER_PERMISSIONS_HEADER, String.join(",", permissions))
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            log.warn("Token校验失败: path={}", path, e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * Check if URL is in whitelist (login, rate query, health, swagger).
     * 检查URL是否在白名单中。
     */
    private boolean isIgnoreUrl(String path) {
        return ignoreAuthUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * Extract Bearer token from Authorization header.
     * 从Authorization头提取Bearer令牌。
     */
    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
