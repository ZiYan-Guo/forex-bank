package com.forex.clearing.domain.model.valueobject;

import com.forex.common.base.domain.BaseValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class PaymentChannel extends BaseValueObject {

    private final String channelCode;
    private final String channelName;
    private final BigDecimal feePerTransaction;
    private final BigDecimal exchangeRateMargin;
    private final String cutOffTime;
    private final boolean sameDaySettlement;
    private final BigDecimal maxAmount;

    public PaymentChannel(String channelCode, String channelName, BigDecimal feePerTransaction,
                          BigDecimal exchangeRateMargin, String cutOffTime, boolean sameDaySettlement,
                          BigDecimal maxAmount) {
        if (channelCode == null || channelCode.isBlank()) {
            throw new IllegalArgumentException("渠道代码不能为空");
        }
        this.channelCode = channelCode;
        this.channelName = channelName;
        this.feePerTransaction = feePerTransaction;
        this.exchangeRateMargin = exchangeRateMargin;
        this.cutOffTime = cutOffTime;
        this.sameDaySettlement = sameDaySettlement;
        this.maxAmount = maxAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentChannel that)) return false;
        return Objects.equals(channelCode, that.channelCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelCode);
    }

    @Override
    public String toString() {
        return "PaymentChannel(channelCode=" + channelCode + ", channelName=" + channelName + ")";
    }
}
