package com.forex.customer.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;

import lombok.Getter;

import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class CustomerId extends BaseValueObject {

    private final Long value;

    private CustomerId(Long value) {
        if (value == null || value <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "客户ID不能为空或无效");
        }
        this.value = value;
    }

    public static CustomerId of(Long value) {
        return new CustomerId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerId that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
