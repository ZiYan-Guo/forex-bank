package com.forex.reporting.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class ReportNo extends BaseValueObject {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final String value;

    private ReportNo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("申报编号不能为空");
        }
        this.value = value;
    }

    public static ReportNo of(String value) {
        return new ReportNo(value);
    }

    public static ReportNo generate(String type) {
        String prefix;
        if ("INWARD".equalsIgnoreCase(type)) {
            prefix = "BOPIN";
        } else if ("OUTWARD".equalsIgnoreCase(type)) {
            prefix = "BOPOUT";
        } else if ("DOMESTIC".equalsIgnoreCase(type)) {
            prefix = "BOPDM";
        } else {
            prefix = "BOP";
        }
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);
        return new ReportNo(prefix + datePart + randomPart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportNo reportNo)) return false;
        return Objects.equals(value, reportNo.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ReportNo(" + value + ")";
    }
}
