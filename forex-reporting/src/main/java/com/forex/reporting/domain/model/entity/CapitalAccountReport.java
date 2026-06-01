package com.forex.reporting.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CapitalAccountReport extends BaseEntity {

    private Long id;
    private String reportNo;
    private Long customerId;
    private String accountNo;
    private String reportType;
    private String transactionType;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private LocalDate transactionDate;
    private String capitalCode;
    private String reportStatus;
    private LocalDateTime submitTime;
    private String regulatoryRef;

    public CapitalAccountReport(Long id, String reportNo, Long customerId,
                                 String accountNo, String reportType,
                                 String transactionType, BigDecimal transactionAmount,
                                 String transactionCurrency, LocalDate transactionDate,
                                 String capitalCode, String reportStatus,
                                 LocalDateTime submitTime, String regulatoryRef) {
        this.id = id;
        this.reportNo = reportNo;
        this.customerId = customerId;
        this.accountNo = accountNo;
        this.reportType = reportType;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionCurrency = transactionCurrency;
        this.transactionDate = transactionDate;
        this.capitalCode = capitalCode;
        this.reportStatus = reportStatus;
        this.submitTime = submitTime;
        this.regulatoryRef = regulatoryRef;
    }
}
