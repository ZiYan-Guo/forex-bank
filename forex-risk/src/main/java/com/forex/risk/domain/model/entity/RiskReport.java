package com.forex.risk.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class RiskReport extends BaseEntity {

    private Long id;
    private String reportNo;
    private String reportType;
    private String reportPeriod;
    private Long customerId;
    private Integer totalTransactions;
    private BigDecimal totalAmount;
    private String reportContent;
    private String reportStatus;
    private LocalDateTime submitTime;
    private Long submitterId;
    private String regulatoryRef;

    public RiskReport(Long id, String reportNo, String reportType, String reportPeriod,
                       Long customerId, Integer totalTransactions, BigDecimal totalAmount,
                       String reportContent, String reportStatus, LocalDateTime submitTime,
                       Long submitterId, String regulatoryRef) {
        this.id = id;
        this.reportNo = reportNo;
        this.reportType = reportType;
        this.reportPeriod = reportPeriod;
        this.customerId = customerId;
        this.totalTransactions = totalTransactions;
        this.totalAmount = totalAmount;
        this.reportContent = reportContent;
        this.reportStatus = reportStatus;
        this.submitTime = submitTime;
        this.submitterId = submitterId;
        this.regulatoryRef = regulatoryRef;
    }
}
