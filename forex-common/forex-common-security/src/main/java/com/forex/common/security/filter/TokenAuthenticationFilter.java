package com.forex.common.security.filter;

import com.forex.common.security.jwt.JwtUtil;
import com.forex.common.security.util.UserContextHolder;
import com.forex.common.security.util.UserInfo;

import io.jsonwebtoken.Claims;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final List<String> SKIP_URLS = Arrays.asList(
            "/actuator/health",
            "/actuator/info",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars",
            "/doc.html"
    );

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public TokenAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SKIP_URLS.stream().anyMatch(p -> pathMatcher.match(p, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"无效或过期的令牌\"}");
                return;
            }
            Claims claims = jwtUtil.parseToken(token);
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(jwtUtil.getUserId(claims));
            userInfo.setUsername(jwtUtil.getUsername(claims));
            userInfo.setRoles(jwtUtil.getRoles(claims));
            userInfo.setPermissions(jwtUtil.getPermissions(claims));
            UserContextHolder.set(userInfo);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"无效或过期的令牌\"}");
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
