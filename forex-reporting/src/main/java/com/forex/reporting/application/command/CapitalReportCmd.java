package com.forex.reporting.application.command;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CapitalReportCmd {

    private Long customerId;
    private String accountNo;
    private String reportType;
    private String transactionType;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private LocalDate transactionDate;
    private String capitalCode;
}
