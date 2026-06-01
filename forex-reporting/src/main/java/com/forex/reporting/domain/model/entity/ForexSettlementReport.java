package com.forex.reporting.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ForexSettlementReport extends BaseEntity {

    private Long id;
    private String reportNo;
    private Long customerId;
    private String exchangeOrderNo;
    private String exchangeType;
    private String dealType;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private BigDecimal cnyAmount;
    private BigDecimal exchangeRate;
    private LocalDate transactionDate;
    private LocalDate settleDate;
    private String settlementCode;
    private String reportStatus;
    private LocalDateTime submitTime;
    private String regulatoryRef;

    public ForexSettlementReport(Long id, String reportNo, Long customerId,
                                  String exchangeOrderNo, String exchangeType,
                                  String dealType, BigDecimal transactionAmount,
                                  String transactionCurrency, BigDecimal cnyAmount,
                                  BigDecimal exchangeRate, LocalDate transactionDate,
                                  LocalDate settleDate, String settlementCode,
                                  String reportStatus, LocalDateTime submitTime,
                                  String regulatoryRef) {
        this.id = id;
        this.reportNo = reportNo;
        this.customerId = customerId;
        this.exchangeOrderNo = exchangeOrderNo;
        this.exchangeType = exchangeType;
        this.dealType = dealType;
        this.transactionAmount = transactionAmount;
        this.transactionCurrency = transactionCurrency;
        this.cnyAmount = cnyAmount;
        this.exchangeRate = exchangeRate;
        this.transactionDate = transactionDate;
        this.settleDate = settleDate;
        this.settlementCode = settlementCode;
        this.reportStatus = reportStatus;
        this.submitTime = submitTime;
        this.regulatoryRef = regulatoryRef;
    }
}
