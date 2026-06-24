package com.forex.common.base.exception;

import com.forex.common.base.result.ResultCode;

public class BusinessException extends BaseException {

    public BusinessException(String message) {
        super(ResultCode.BUSINESS_ERROR, message);
    }

    public BusinessException(int code, String message) {
        super(code, message);
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode);
    }

    public BusinessException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }
}
