package com.forex.common.base.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILURE(500, "操作失败"),
    VALIDATE_FAIL(400, "参数校验失败"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    IDEMPOTENT_REPEATED(409, "重复请求"),
    LOCK_FAILED(423, "获取锁失败"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    BUSINESS_ERROR(10001, "业务异常"),
    RISK_INTERCEPT(10002, "风控拦截"),
    INSUFFICIENT_QUOTA(10003, "额度不足"),
    EXCHANGE_RATE_EXPIRED(10004, "汇率已过期"),
    ORDER_STATUS_ERROR(10005, "订单状态异常"),
    ACCOUNT_ERROR(10006, "账户异常"),
    ACCOUNT_NOT_FOUND(10007, "账户不存在"),

    REMOTE_CALL_FAIL(20001, "远程调用失败");

    private final int code;
    private final String message;
}
