package com.forex.settlement.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class LcAmount extends BaseValueObject {

    private final BigDecimal amount;
    private final String currency;

    private LcAmount(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("信用证金额必须大于0");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("币种不能为空");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static LcAmount of(BigDecimal amount, String currency) {
        return new LcAmount(amount, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LcAmount lcAmount)) return false;
        return Objects.equals(amount, lcAmount.amount) && Objects.equals(currency, lcAmount.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
