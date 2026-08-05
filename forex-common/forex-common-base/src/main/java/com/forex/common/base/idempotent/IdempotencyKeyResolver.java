package com.forex.common.base.idempotent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.forex.common.base.util.SpelUtil;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Resolves a stable idempotency key for write operations.
 * 为写操作解析稳定的幂等键。
 *
 * <p>This class follows the Resolver pattern so the AOP aspect only coordinates Redis access.
 * The resolver prefers an explicit request token and falls back to a deterministic argument
 * fingerprint for internal calls or legacy clients.
 * 该类采用 Resolver 模式，使切面只负责协调 Redis；优先使用显式请求令牌，
 * 对内部调用或旧客户端则回退到稳定的方法参数指纹。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyKeyResolver {

    public static final String IDEMPOTENCY_HEADER = "Idempotency-Key";
    public static final String LEGACY_IDEMPOTENCY_HEADER = "X-Idempotency-Key";

    private static final int MAX_BUSINESS_KEY_LENGTH = 128;
    private static final int DIGEST_LENGTH = 16;

    private final ObjectMapper objectMapper;

    /**
     * Resolves business scope plus request token/fingerprint.
     * 解析业务范围，并拼接请求令牌或请求指纹。
     *
     * @param method annotated method / 被注解的方法
     * @param args method arguments / 方法参数
     * @param expression business key SpEL / 业务键 SpEL 表达式
     * @return stable idempotency key / 稳定幂等键
     */
    public String resolve(Method method, Object[] args, String expression) {
        String businessKey = normalizeBusinessKey(
                SpelUtil.resolveTemplate(method, args, expression));
        String requestToken = resolveRequestToken();
        String requestPart = StringUtils.hasText(requestToken)
                ? digest(requestToken)
                : buildArgumentFingerprint(method, args);
        return businessKey + ":" + requestPart;
    }

    /**
     * Returns a short digest for logs to avoid exposing request content.
     * 返回短摘要用于日志，避免泄露请求内容。
     */
    public String digest(String value) {
        return sha256(value).substring(0, DIGEST_LENGTH);
    }

    private String resolveRequestToken() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader(IDEMPOTENCY_HEADER);
        return StringUtils.hasText(token)
                ? token
                : request.getHeader(LEGACY_IDEMPOTENCY_HEADER);
    }

    private String buildArgumentFingerprint(Method method, Object[] args) {
        try {
            String methodName = method.getDeclaringClass().getName() + "#" + method.getName();
            String arguments = objectMapper
                    .copy()
                    .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                    .writeValueAsString(args);
            return digest(methodName + "|" + arguments);
        } catch (JsonProcessingException exception) {
            // Fallback is deterministic for simple DTOs and primitive arguments.
            // 兜底逻辑针对简单DTO和基础参数保持确定性，避免序列化异常阻断主流程。
            log.debug(
                    "Idempotency fingerprint serialization failed / 幂等指纹序列化失败, method={}",
                    method.getDeclaringClass().getSimpleName() + "." + method.getName());
            return digest(method.getName() + "|" + String.valueOf(java.util.Arrays.deepHashCode(args)));
        }
    }

    private String normalizeBusinessKey(String businessKey) {
        if (!StringUtils.hasText(businessKey)) {
            return "unknown";
        }
        String normalized = businessKey.replaceAll("[^A-Za-z0-9:_\\-.]", "_");
        return normalized.length() <= MAX_BUSINESS_KEY_LENGTH
                ? normalized
                : normalized.substring(0, MAX_BUSINESS_KEY_LENGTH);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable / SHA-256算法不可用", exception);
        }
    }
}
