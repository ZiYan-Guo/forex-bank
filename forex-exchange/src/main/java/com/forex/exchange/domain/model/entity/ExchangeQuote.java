package com.forex.exchange.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ExchangeQuote extends BaseEntity {

    private Long id;
    private Long customerId;
    private String baseCurrency;
    private String quoteCurrency;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private LocalDateTime quoteTime;
    private LocalDateTime expireTime;
    private Integer quoteStatus;

    public ExchangeQuote(Long id, Long customerId, String baseCurrency, String quoteCurrency,
                          BigDecimal bidRate, BigDecimal askRate, BigDecimal midRate,
                          LocalDateTime quoteTime, LocalDateTime expireTime, Integer quoteStatus) {
        this.id = id;
        this.customerId = customerId;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.bidRate = bidRate;
        this.askRate = askRate;
        this.midRate = midRate;
        this.quoteTime = quoteTime;
        this.expireTime = expireTime;
        this.quoteStatus = quoteStatus;
    }
}
