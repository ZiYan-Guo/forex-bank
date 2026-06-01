package com.forex.exchange.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class OrderNo extends BaseValueObject {

    private final String value;

    private OrderNo(String value) {
        this.value = value;
    }

    public static OrderNo generate(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String random = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        return new OrderNo(prefix + timestamp + random);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderNo orderNo)) return false;
        return Objects.equals(value, orderNo.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
