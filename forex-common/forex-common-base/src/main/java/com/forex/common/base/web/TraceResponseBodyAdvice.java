package com.forex.common.base.web;

import com.forex.common.base.context.TraceContext;
import com.forex.common.base.result.R;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Adds trace id to unified API responses.
 * 为统一响应体自动补充追踪ID。
 *
 * <p>This Advice keeps controllers focused on business orchestration and avoids repetitive
 * response decoration code.
 * 该增强器让控制器专注业务编排，避免在每个接口重复设置 traceId。</p>
 */
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class TraceResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof R<?> result && result.getTraceId() == null) {
            result.traceId(TraceContext.getTraceId());
        }
        return body;
    }
}
