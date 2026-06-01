package com.forex.valuation.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class FairValue extends BaseValueObject {

    private final BigDecimal value;

    private FairValue(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("公允价值不能为空");
        }
        this.value = value;
    }

    public static FairValue of(BigDecimal value) {
        return new FairValue(value);
    }

    public static FairValue zero() {
        return new FairValue(BigDecimal.ZERO);
    }

    public FairValue add(FairValue other) {
        if (other == null) {
            throw new IllegalArgumentException("加数不能为空");
        }
        return new FairValue(this.value.add(other.value));
    }

    public FairValue subtract(FairValue other) {
        if (other == null) {
            throw new IllegalArgumentException("减数不能为空");
        }
        return new FairValue(this.value.subtract(other.value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FairValue that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
