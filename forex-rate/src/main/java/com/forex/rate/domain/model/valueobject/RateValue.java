package com.forex.rate.domain.model.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

import com.forex.common.base.domain.BaseValueObject;

import lombok.Getter;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class RateValue extends BaseValueObject {

    private static final long serialVersionUID = 1L;

    private final BigDecimal value;

    private RateValue(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "Rate value must be greater than 0");
        }
        this.value = value;
    }

    public static RateValue of(BigDecimal value) {
        return new RateValue(value);
    }

    public RateValue addSpread(BigDecimal spread) {
        if (spread == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "Spread must not be null");
        }
        return new RateValue(this.value.add(spread));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RateValue that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
