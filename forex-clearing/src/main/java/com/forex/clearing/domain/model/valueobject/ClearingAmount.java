package com.forex.clearing.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class ClearingAmount extends BaseValueObject {

    private final BigDecimal amount;
    private final String currency;

    private ClearingAmount(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "金额不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "币种不能为空");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static ClearingAmount of(BigDecimal amount, String currency) {
        return new ClearingAmount(amount, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClearingAmount that)) return false;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return "ClearingAmount(amount=" + amount + ", currency=" + currency + ")";
    }
}
