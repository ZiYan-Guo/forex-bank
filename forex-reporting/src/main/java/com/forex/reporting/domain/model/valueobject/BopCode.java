package com.forex.reporting.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.util.Objects;

@Getter
public class BopCode extends BaseValueObject {

    private final String value;

    private BopCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("国际收支申报代码不能为空");
        }
        this.value = value;
    }

    public static BopCode of(String value) {
        return new BopCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BopCode bopCode)) return false;
        return Objects.equals(value, bopCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BopCode(" + value + ")";
    }
}
