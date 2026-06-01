package com.forex.position.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class PositionAmount extends BaseValueObject {

    private final BigDecimal amount;

    private PositionAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("头寸金额不能为空");
        }
        this.amount = amount;
    }

    public static PositionAmount of(BigDecimal amount) {
        return new PositionAmount(amount);
    }

    public static PositionAmount zero() {
        return new PositionAmount(BigDecimal.ZERO);
    }

    public PositionAmount add(PositionAmount other) {
        if (other == null) {
            throw new IllegalArgumentException("加数不能为空");
        }
        return new PositionAmount(this.amount.add(other.amount));
    }

    public PositionAmount subtract(PositionAmount other) {
        if (other == null) {
            throw new IllegalArgumentException("减数不能为空");
        }
        return new PositionAmount(this.amount.subtract(other.amount));
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionAmount that)) return false;
        return Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
