package com.forex.trading.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class StrikePrice extends BaseValueObject {

    private final BigDecimal value;

    private StrikePrice(BigDecimal value) {
        this.value = value;
    }

    public static StrikePrice of(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("执行价不能为空");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("执行价必须大于0");
        }
        return new StrikePrice(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StrikePrice that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
