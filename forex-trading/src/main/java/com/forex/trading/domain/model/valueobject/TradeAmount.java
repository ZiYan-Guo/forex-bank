package com.forex.trading.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class TradeAmount extends BaseValueObject {

    private final BigDecimal amount;
    private final String currency;

    private TradeAmount(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static TradeAmount of(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("货币不能为空");
        }
        return new TradeAmount(amount, currency);
    }

    public TradeAmount add(TradeAmount other) {
        assertSameCurrency(other);
        return new TradeAmount(this.amount.add(other.amount), this.currency);
    }

    public TradeAmount subtract(TradeAmount other) {
        assertSameCurrency(other);
        return new TradeAmount(this.amount.subtract(other.amount), this.currency);
    }

    public TradeAmount multiply(BigDecimal rate) {
        if (rate == null) {
            throw new IllegalArgumentException("汇率不能为空");
        }
        return new TradeAmount(this.amount.multiply(rate), this.currency);
    }

    private void assertSameCurrency(TradeAmount other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("货币不匹配: " + this.currency + " vs " + other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeAmount that)) return false;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
