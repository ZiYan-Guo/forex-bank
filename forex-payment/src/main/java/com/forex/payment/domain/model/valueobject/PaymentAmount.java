package com.forex.payment.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class PaymentAmount extends BaseValueObject {

    private final BigDecimal amount;
    private final String currency;

    private PaymentAmount(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "金额不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "币种不能为空");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static PaymentAmount of(BigDecimal amount, String currency) {
        return new PaymentAmount(amount, currency);
    }

    public static PaymentAmount zero(String currency) {
        return new PaymentAmount(BigDecimal.ZERO, currency);
    }

    public PaymentAmount add(PaymentAmount other) {
        if (other == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "加数不能为空");
        }
        if (!this.currency.equals(other.currency)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "币种不一致，不能相加: " + this.currency + " vs " + other.currency);
        }
        return new PaymentAmount(this.amount.add(other.amount), this.currency);
    }

    public PaymentAmount subtract(PaymentAmount other) {
        if (other == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "减数不能为空");
        }
        if (!this.currency.equals(other.currency)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "币种不一致，不能相减: " + this.currency + " vs " + other.currency);
        }
        return new PaymentAmount(this.amount.subtract(other.amount), this.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentAmount that)) return false;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return "PaymentAmount(amount=" + amount + ", currency=" + currency + ")";
    }
}
