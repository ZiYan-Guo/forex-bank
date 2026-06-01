package com.forex.settlement.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class LcNo extends BaseValueObject {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final String value;

    private LcNo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("信用证编号不能为空");
        }
        this.value = value;
    }

    public static LcNo of(String value) {
        return new LcNo(value);
    }

    public static LcNo generate(String type) {
        String prefix;
        if ("IMPORT".equalsIgnoreCase(type)) {
            prefix = "LCIM";
        } else if ("EXPORT".equalsIgnoreCase(type)) {
            prefix = "LCEX";
        } else if ("STANDBY".equalsIgnoreCase(type)) {
            prefix = "LCSB";
        } else if ("DOMESTIC".equalsIgnoreCase(type)) {
            prefix = "LCDM";
        } else {
            prefix = "LC";
        }
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);
        return new LcNo(prefix + datePart + randomPart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LcNo lcNo)) return false;
        return Objects.equals(value, lcNo.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "LcNo(" + value + ")";
    }
}
