package com.forex.trading.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class SwapPoints extends BaseValueObject {

    private final BigDecimal value;

    private SwapPoints(BigDecimal value) {
        this.value = value;
    }

    public static SwapPoints of(BigDecimal value) {
        if (value == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "掉期点不能为空");
        }
        return new SwapPoints(value);
    }

    public BigDecimal applyToRate(BigDecimal rate) {
        if (rate == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "即期汇率不能为空");
        }
        return rate.add(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SwapPoints that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
