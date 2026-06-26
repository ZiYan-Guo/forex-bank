package com.forex.account.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class Money extends BaseValueObject {

    private final BigDecimal amount;
    private final String currency;

    private Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "金额不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "币种不能为空");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        if (other == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "加数不能为空");
        }
        if (!this.currency.equals(other.currency)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "币种不一致，不能相加: " + this.currency + " vs " + other.currency);
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        if (other == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "减数不能为空");
        }
        if (!this.currency.equals(other.currency)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "币种不一致，不能相减: " + this.currency + " vs " + other.currency);
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money other) {
        if (other == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "比较对象不能为空");
        }
        if (!this.currency.equals(other.currency)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "币种不一致，不能比较");
        }
        return this.amount.compareTo(other.amount) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money that)) return false;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
