package com.forex.risk.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class RiskScore extends BaseValueObject {

    private final BigDecimal value;

    private RiskScore(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "风险评分不能为空且不能为负数");
        }
        this.value = value;
    }

    public static RiskScore of(BigDecimal value) {
        return new RiskScore(value);
    }

    public boolean isHigh() {
        return value.compareTo(new BigDecimal("70")) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RiskScore riskScore)) return false;
        return Objects.equals(value, riskScore.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "RiskScore(" + value + ")";
    }
}
