package com.forex.settlement.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class BankGuarantee extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String guaranteeNo;
    private Long customerId;
    private String guaranteeType;
    private BigDecimal guaranteeAmount;
    private String guaranteeCurrency;
    private String beneficiaryInfo;
    private LocalDate issueDate;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private LocalDate claimExpiryDate;
    private String counterGuaranteeNo;
    private String guaranteeFormat;
    private String guaranteeStatus;
    private BigDecimal feeAmount;
    private BigDecimal commissionRate;
    private Long operatorId;
    private String swiftRef;
    private String remark;

    public BankGuarantee(Long id, String guaranteeNo, Long customerId,
                          String guaranteeType, BigDecimal guaranteeAmount,
                          String guaranteeCurrency, String beneficiaryInfo,
                          LocalDate issueDate, LocalDate effectiveDate,
                          LocalDate expiryDate, LocalDate claimExpiryDate,
                          String counterGuaranteeNo, String guaranteeFormat,
                          String guaranteeStatus, BigDecimal feeAmount,
                          BigDecimal commissionRate, Long operatorId,
                          String swiftRef, String remark) {
        this.id = id;
        this.guaranteeNo = guaranteeNo;
        this.customerId = customerId;
        this.guaranteeType = guaranteeType;
        this.guaranteeAmount = guaranteeAmount;
        this.guaranteeCurrency = guaranteeCurrency;
        this.beneficiaryInfo = beneficiaryInfo;
        this.issueDate = issueDate;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.claimExpiryDate = claimExpiryDate;
        this.counterGuaranteeNo = counterGuaranteeNo;
        this.guaranteeFormat = guaranteeFormat;
        this.guaranteeStatus = guaranteeStatus;
        this.feeAmount = feeAmount;
        this.commissionRate = commissionRate;
        this.operatorId = operatorId;
        this.swiftRef = swiftRef;
        this.remark = remark;
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }
}
