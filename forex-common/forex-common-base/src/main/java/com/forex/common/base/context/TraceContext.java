package com.forex.common.base.context;

/**
 * Lightweight request trace context backed by ThreadLocal.
 * 基于 ThreadLocal 的轻量级请求追踪上下文。
 *
 * <p>It keeps the common base module independent from security/user modules while still allowing
 * filters, response advices, and exception handlers to read the same trace identifiers.
 * 该上下文不依赖安全或用户模块，过滤器、响应增强器和异常处理器可共享同一批追踪标识。</p>
 */
public final class TraceContext {

    private static final ThreadLocal<String> TRACE_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> REQUEST_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ID_HOLDER = new ThreadLocal<>();

    private TraceContext() {
    }

    public static void setTraceId(String traceId) {
        TRACE_ID_HOLDER.set(traceId);
    }

    public static String getTraceId() {
        return TRACE_ID_HOLDER.get();
    }

    public static void setRequestId(String requestId) {
        REQUEST_ID_HOLDER.set(requestId);
    }

    public static String getRequestId() {
        return REQUEST_ID_HOLDER.get();
    }

    public static void setUserId(String userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static String getUserId() {
        return USER_ID_HOLDER.get();
    }

    /**
     * Clears all thread-local values after request completion.
     * 请求完成后清理线程变量，避免容器线程复用导致上下文串扰。
     */
    public static void clear() {
        TRACE_ID_HOLDER.remove();
        REQUEST_ID_HOLDER.remove();
        USER_ID_HOLDER.remove();
    }
}
