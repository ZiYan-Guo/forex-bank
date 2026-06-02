package com.forex.valuation.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class ValuationInput {

    private Long tradeId;
    private String tradeType;
    private String callPut;
    private BigDecimal notionalAmount;
    private BigDecimal strikePrice;
    private BigDecimal spotRate;
    @Builder.Default
    private BigDecimal domesticRate = new BigDecimal("0.03");
    @Builder.Default
    private BigDecimal foreignRate = new BigDecimal("0.02");
    @Builder.Default
    private BigDecimal volatility = new BigDecimal("0.15");
    private double timeToMaturity;
    private BigDecimal forwardRate;
    private LocalDate valuationDate;
}
