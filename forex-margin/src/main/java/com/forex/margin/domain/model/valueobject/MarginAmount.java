package com.forex.margin.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class MarginAmount extends BaseValueObject {

    private final BigDecimal amount;
    private final String currency;

    private MarginAmount(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("币种不能为空");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static MarginAmount of(BigDecimal amount, String currency) {
        return new MarginAmount(amount, currency);
    }

    public static MarginAmount zero(String currency) {
        return new MarginAmount(BigDecimal.ZERO, currency);
    }

    public MarginAmount add(MarginAmount other) {
        if (other == null) {
            throw new IllegalArgumentException("加数不能为空");
        }
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("币种不一致，不能相加: " + this.currency + " vs " + other.currency);
        }
        return new MarginAmount(this.amount.add(other.amount), this.currency);
    }

    public MarginAmount subtract(MarginAmount other) {
        if (other == null) {
            throw new IllegalArgumentException("减数不能为空");
        }
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("币种不一致，不能相减: " + this.currency + " vs " + other.currency);
        }
        return new MarginAmount(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarginAmount that)) return false;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
