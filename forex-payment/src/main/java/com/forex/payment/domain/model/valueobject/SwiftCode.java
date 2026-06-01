package com.forex.payment.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SwiftCode extends BaseValueObject {

    private final String code;

    private SwiftCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("SWIFT代码不能为空");
        }
        String trimmed = code.trim().toUpperCase();
        if (trimmed.length() != 8 && trimmed.length() != 11) {
            throw new IllegalArgumentException("SWIFT代码长度必须为8位或11位: " + code);
        }
        this.code = trimmed;
    }

    public static SwiftCode of(String code) {
        return new SwiftCode(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SwiftCode that)) return false;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "SwiftCode(" + code + ")";
    }
}
