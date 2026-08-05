package com.forex.common.base.web;

import com.forex.common.base.constant.SystemConstants;
import com.forex.common.base.context.TraceContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet request trace filter.
 * Servlet 请求追踪过滤器。
 *
 * <p>The filter follows the Chain of Responsibility pattern: it prepares trace information before
 * the controller executes, then records latency and clears context after the downstream chain
 * finishes. It intentionally logs path only and avoids query strings to reduce sensitive-data risk.
 * 该过滤器采用责任链模式：控制器执行前准备追踪信息，下游链路完成后记录耗时并清理上下文。
 * 日志只记录路径不记录查询串，降低敏感数据泄露风险。</p>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RequestTraceFilter extends OncePerRequestFilter {

    private static final String MDC_TRACE_ID = "traceId";
    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_USER_ID = "userId";
    private static final long SLOW_REQUEST_THRESHOLD_MS = 1_000L;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String traceId = firstNotBlank(
                request.getHeader(SystemConstants.TRACE_ID_HEADER),
                request.getHeader(SystemConstants.REQUEST_ID_HEADER),
                newTraceId());
        String requestId = firstNotBlank(request.getHeader(SystemConstants.REQUEST_ID_HEADER), traceId);
        String userId = firstNotBlank(request.getHeader(SystemConstants.USER_ID_HEADER), "anonymous");

        bindTraceContext(traceId, requestId, userId);
        response.setHeader(SystemConstants.TRACE_ID_HEADER, traceId);
        response.setHeader(SystemConstants.REQUEST_ID_HEADER, requestId);

        try {
            log.info(
                    "HTTP request started / HTTP请求开始, method={}, uri={}, userId={}, traceId={}, requestId={}",
                    request.getMethod(), request.getRequestURI(), userId, traceId, requestId);
            filterChain.doFilter(request, response);
        } finally {
            long elapsedMs = System.currentTimeMillis() - startTime;
            if (elapsedMs >= SLOW_REQUEST_THRESHOLD_MS) {
                log.warn(
                        "Slow HTTP request completed / 慢HTTP请求完成, method={}, uri={}, status={}, elapsedMs={}, traceId={}, requestId={}",
                        request.getMethod(), request.getRequestURI(), response.getStatus(), elapsedMs, traceId, requestId);
            } else {
                log.info(
                        "HTTP request completed / HTTP请求完成, method={}, uri={}, status={}, elapsedMs={}, traceId={}, requestId={}",
                        request.getMethod(), request.getRequestURI(), response.getStatus(), elapsedMs, traceId, requestId);
            }
            clearTraceContext();
        }
    }

    private void bindTraceContext(String traceId, String requestId, String userId) {
        TraceContext.setTraceId(traceId);
        TraceContext.setRequestId(requestId);
        TraceContext.setUserId(userId);
        MDC.put(MDC_TRACE_ID, traceId);
        MDC.put(MDC_REQUEST_ID, requestId);
        MDC.put(MDC_USER_ID, userId);
    }

    private void clearTraceContext() {
        TraceContext.clear();
        MDC.remove(MDC_TRACE_ID);
        MDC.remove(MDC_REQUEST_ID);
        MDC.remove(MDC_USER_ID);
    }

    private String firstNotBlank(String first, String second) {
        return StringUtils.hasText(first) ? first : second;
    }

    private String firstNotBlank(String first, String second, String fallback) {
        if (StringUtils.hasText(first)) {
            return first;
        }
        return StringUtils.hasText(second) ? second : fallback;
    }

    private String newTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
