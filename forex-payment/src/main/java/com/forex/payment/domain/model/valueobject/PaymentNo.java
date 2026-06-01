package com.forex.payment.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class PaymentNo extends BaseValueObject {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final String value;

    private PaymentNo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("支付编号不能为空");
        }
        this.value = value;
    }

    public static PaymentNo of(String value) {
        return new PaymentNo(value);
    }

    public static PaymentNo generate(String direction) {
        if (direction == null || direction.isBlank()) {
            throw new IllegalArgumentException("支付方向不能为空");
        }
        String prefix = "INWARD".equalsIgnoreCase(direction) ? "PMTIN" : "PMTOUT";
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);
        return new PaymentNo(prefix + datePart + randomPart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentNo that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "PaymentNo(" + value + ")";
    }
}
