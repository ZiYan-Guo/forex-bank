package com.forex.reporting.application.command;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BopReportCmd {

    private Long customerId;
    private String customerName;
    private String transactionNo;
    private String transactionType;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private BigDecimal cnyAmount;
    private BigDecimal exchangeRate;
    private LocalDate transactionDate;
    private LocalDate settlementDate;
    private String bopCode;
    private String bopName;
    private String counterpartyCountry;
}
