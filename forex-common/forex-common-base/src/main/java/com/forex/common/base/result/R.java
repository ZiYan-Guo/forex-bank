package com.forex.common.base.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified API response wrapper. All controllers return R&lt;T&gt; for consistent format.
 * 统一API响应体。所有Controller返回此格式。
 */
@Schema(description = "统一响应体")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {

    @Schema(description = "状态码", example = "200")
    private int code;

    @Schema(description = "消息", example = "操作成功")
    private String message;

    @Schema(description = "数据")
    private T data;

    @Schema(description = "请求追踪ID")
    private String traceId;

    @Schema(description = "时间戳")
    private long timestamp;

    /**
     * Success response builders.
     * 成功响应构建器。
     */
    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null, null, System.currentTimeMillis());
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, null, System.currentTimeMillis());
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), message, data, null, System.currentTimeMillis());
    }

    /**
     * Success response without data body.
     * 无数据的成功响应。
     */
    public static R<Void> okMsg(String message) {
        return new R<>(ResultCode.SUCCESS.getCode(), message, null, null, System.currentTimeMillis());
    }

    /**
     * Error response builders.
     * 错误响应构建器。
     */
    public static <T> R<T> fail() {
        return new R<>(ResultCode.FAILURE.getCode(), ResultCode.FAILURE.getMessage(), null, null, System.currentTimeMillis());
    }

    public static <T> R<T> fail(String message) {
        return new R<>(ResultCode.FAILURE.getCode(), message, null, null, System.currentTimeMillis());
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null, null, System.currentTimeMillis());
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode.getCode(), resultCode.getMessage(), null, null, System.currentTimeMillis());
    }

    public R<T> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    /**
     * Check if response code is 200.
     * 检查响应是否成功。
     */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}
