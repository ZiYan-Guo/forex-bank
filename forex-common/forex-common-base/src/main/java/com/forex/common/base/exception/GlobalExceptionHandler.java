package com.forex.common.base.exception;

import com.forex.common.base.context.TraceContext;
import com.forex.common.base.result.R;
import com.forex.common.base.result.ResultCode;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBusinessException(BusinessException e) {
        log.warn(
                "Business exception / 业务异常, code={}, message={}, traceId={}",
                e.getCode(), e.getMessage(), TraceContext.getTraceId());
        return R.<Void>fail(e.getCode(), e.getMessage()).traceId(TraceContext.getTraceId());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn(
                "Illegal argument / 参数异常, message={}, traceId={}",
                e.getMessage(), TraceContext.getTraceId());
        return R.<Void>fail(ResultCode.VALIDATE_FAIL.getCode(), e.getMessage()).traceId(TraceContext.getTraceId());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("Validation failed / 参数校验失败, message={}, traceId={}", msg, TraceContext.getTraceId());
        return R.<Void>fail(ResultCode.VALIDATE_FAIL.getCode(), msg).traceId(TraceContext.getTraceId());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("Request body parse failed / 请求体解析失败, message={}, traceId={}",
                e.getMessage(), TraceContext.getTraceId());
        return R.<Void>fail(ResultCode.VALIDATE_FAIL.getCode(), "请求格式错误").traceId(TraceContext.getTraceId());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("Missing request parameter / 缺少必需参数, parameter={}, traceId={}",
                e.getParameterName(), TraceContext.getTraceId());
        return R.<Void>fail(ResultCode.VALIDATE_FAIL.getCode(), "缺少必需参数: " + e.getParameterName())
                .traceId(TraceContext.getTraceId());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("Unsupported request method / 不支持的请求方法, method={}, traceId={}",
                e.getMethod(), TraceContext.getTraceId());
        return R.<Void>fail(405, "不支持的请求方法: " + e.getMethod()).traceId(TraceContext.getTraceId());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public R<Void> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Data integrity violation / 数据完整性冲突, traceId={}", TraceContext.getTraceId(), e);
        return R.<Void>fail(ResultCode.BUSINESS_ERROR.getCode(), "数据操作冲突，请检查后重试")
                .traceId(TraceContext.getTraceId());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e) {
        log.error("System exception / 系统异常, traceId={}", TraceContext.getTraceId(), e);
        return R.<Void>fail(ResultCode.FAILURE.getCode(), "系统繁忙，请稍后再试")
                .traceId(TraceContext.getTraceId());
    }
}
