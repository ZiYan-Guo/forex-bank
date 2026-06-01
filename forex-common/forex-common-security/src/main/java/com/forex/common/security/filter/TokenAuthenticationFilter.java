package com.forex.common.security.filter;

import com.forex.common.security.jwt.JwtUtil;
import com.forex.common.security.util.UserContextHolder;
import com.forex.common.security.util.UserInfo;

import io.jsonwebtoken.Claims;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component
/**
 * Token authentication filter for Spring MVC (non-Gateway) services.
 * Validates JWT and sets UserContext. Provides defense-in-depth when Gateway is bypassed.
 * Spring MVC用鉴权过滤器，自验证JWT并设置用户上下文。网关被绕过时的纵深防御。
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Name";
    private static final String USER_ROLES_HEADER = "X-User-Roles";
    private static final String USER_PERMISSIONS_HEADER = "X-User-Permissions";

    private final JwtUtil jwtUtil;

    public TokenAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Filter each request - extract token, validate JWT, set ThreadLocal context.
     * 每个请求过滤：提取token，校验JWT，设置线程级上下文。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.parseToken(token);
                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(jwtUtil.getUserId(claims));
                userInfo.setUsername(jwtUtil.getUsername(claims));
                userInfo.setRoles(jwtUtil.getRoles(claims));
                userInfo.setPermissions(jwtUtil.getPermissions(claims));
                UserContextHolder.set(userInfo);
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
