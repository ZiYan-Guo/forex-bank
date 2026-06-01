package com.forex.rate.domain.model.aggregate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

@Getter
public class ExchangeRate extends BaseAggregate {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String currencyPair;
    private String baseCurrency;
    private String quoteCurrency;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private BigDecimal spread;
    private String rateSource;
    private LocalDate rateDate;
    private LocalDateTime rateTime;
    private Integer status;

    private ExchangeRate() {
        super();
    }

    public static ExchangeRate create(String currencyPair, String baseCurrency, String quoteCurrency,
                                       BigDecimal bidRate, BigDecimal askRate, String rateSource) {
        ExchangeRate rate = new ExchangeRate();
        rate.currencyPair = currencyPair;
        rate.baseCurrency = baseCurrency;
        rate.quoteCurrency = quoteCurrency;
        rate.bidRate = bidRate;
        rate.askRate = askRate;
        rate.midRate = bidRate.add(askRate).divide(new BigDecimal("2"), 8, RoundingMode.HALF_UP);
        rate.spread = askRate.subtract(bidRate);
        rate.rateSource = rateSource;
        rate.rateDate = LocalDate.now();
        rate.rateTime = LocalDateTime.now();
        rate.status = 1;
        rate.validate();
        return rate;
    }

    public void invalidate() {
        this.status = 0;
        markUpdated();
    }

    public boolean isExpired(Duration ttl) {
        if (rateTime == null) {
            return true;
        }
        return Duration.between(rateTime, LocalDateTime.now()).compareTo(ttl) > 0;
    }

    @Override
    protected void validate() {
        if (currencyPair == null || currencyPair.isBlank()) {
            throw new IllegalArgumentException("currencyPair must not be blank");
        }
        if (bidRate == null || bidRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("bidRate must be greater than 0");
        }
        if (askRate == null || askRate.compareTo(bidRate) <= 0) {
            throw new IllegalArgumentException("askRate must be greater than bidRate");
        }
    }
}
