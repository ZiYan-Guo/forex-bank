package com.forex.gateway.filter;

import com.forex.common.base.constant.SystemConstants;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Propagates request identifiers through the reactive gateway.
 * 在响应式网关中生成并透传请求追踪标识。
 *
 * <p>The downstream Servlet services use the same headers, so a browser request can be traced
 * across gateway and business service logs.
 * 下游 Servlet 服务使用相同请求头，因此浏览器请求可以串联网关和业务服务日志。</p>
 */
@Slf4j
@Component
public class TraceGlobalFilter implements GlobalFilter, Ordered {

    private static final long SLOW_GATEWAY_REQUEST_THRESHOLD_MS = 1_000L;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = resolveRequestId(exchange);
        String traceId = resolveTraceId(exchange, requestId);
        String path = exchange.getRequest().getURI().getPath();
        long startTime = System.currentTimeMillis();

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(SystemConstants.REQUEST_ID_HEADER, requestId)
                .header(SystemConstants.TRACE_ID_HEADER, traceId)
                .build();
        ServerWebExchange tracedExchange = exchange.mutate().request(request).build();
        tracedExchange.getResponse().getHeaders().set(SystemConstants.REQUEST_ID_HEADER, requestId);
        tracedExchange.getResponse().getHeaders().set(SystemConstants.TRACE_ID_HEADER, traceId);

        MDC.put("traceId", traceId);
        MDC.put("requestId", requestId);
        log.info(
                "Gateway request started / 网关请求开始, method={}, path={}, traceId={}, requestId={}",
                request.getMethod(), path, traceId, requestId);

        return chain.filter(tracedExchange)
                .doFinally(signalType -> {
                    long elapsedMs = System.currentTimeMillis() - startTime;
                    if (elapsedMs >= SLOW_GATEWAY_REQUEST_THRESHOLD_MS) {
                        log.warn(
                                "Slow gateway request completed / 慢网关请求完成, method={}, path={}, status={}, elapsedMs={}, traceId={}, requestId={}",
                                request.getMethod(), path, tracedExchange.getResponse().getStatusCode(),
                                elapsedMs, traceId, requestId);
                    } else {
                        log.info(
                                "Gateway request completed / 网关请求完成, method={}, path={}, status={}, elapsedMs={}, traceId={}, requestId={}",
                                request.getMethod(), path, tracedExchange.getResponse().getStatusCode(),
                                elapsedMs, traceId, requestId);
                    }
                    MDC.remove("traceId");
                    MDC.remove("requestId");
                });
    }

    private String resolveRequestId(ServerWebExchange exchange) {
        String requestId = exchange.getRequest().getHeaders().getFirst(SystemConstants.REQUEST_ID_HEADER);
        return StringUtils.hasText(requestId) ? requestId : newTraceId();
    }

    private String resolveTraceId(ServerWebExchange exchange, String requestId) {
        String traceId = exchange.getRequest().getHeaders().getFirst(SystemConstants.TRACE_ID_HEADER);
        return StringUtils.hasText(traceId) ? traceId : requestId;
    }

    private String newTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
