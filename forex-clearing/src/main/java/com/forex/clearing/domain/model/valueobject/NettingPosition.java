package com.forex.clearing.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class NettingPosition extends BaseValueObject {

    private final String currency;
    private final BigDecimal totalPay;
    private final BigDecimal totalReceive;
    private final BigDecimal netPosition;

    private NettingPosition(String currency, BigDecimal totalPay, BigDecimal totalReceive) {
        this.currency = currency;
        this.totalPay = totalPay;
        this.totalReceive = totalReceive;
        this.netPosition = totalReceive.subtract(totalPay);
    }

    public static NettingPosition of(String currency, BigDecimal totalPay, BigDecimal totalReceive) {
        return new NettingPosition(currency, totalPay, totalReceive);
    }

    public NettingPosition add(BigDecimal pay, BigDecimal receive) {
        BigDecimal newPay = this.totalPay.add(pay != null ? pay : BigDecimal.ZERO);
        BigDecimal newReceive = this.totalReceive.add(receive != null ? receive : BigDecimal.ZERO);
        return new NettingPosition(this.currency, newPay, newReceive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NettingPosition that)) return false;
        return Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency);
    }
}
