package com.forex.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Security headers filter. Adds XSS, Clickjacking, MIME-sniffing protection headers.
 * 安全响应头过滤器。添加XSS/点击劫持/MIME嗅探防护头。
 */
@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    /**
     * Inject security headers into every response.
     * 注入安全响应头到每个响应。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        headers.add("Referrer-Policy", "strict-origin-when-cross-origin");
        headers.add("Cache-Control", "no-store, no-cache, must-revalidate, private");
        headers.add("Pragma", "no-cache");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
