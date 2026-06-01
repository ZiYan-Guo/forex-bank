package com.forex.reporting.application.command;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SettlementReportCmd {

    private Long customerId;
    private String exchangeOrderNo;
    private String exchangeType;
    private String dealType;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private BigDecimal cnyAmount;
    private BigDecimal exchangeRate;
    private LocalDate transactionDate;
    private String settlementCode;
}
