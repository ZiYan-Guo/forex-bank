package com.forex.settlement.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import com.forex.common.base.exception.BusinessException;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Bank guarantee entity with explicit lifecycle behavior.
 * 银行保函实体，封装明确的生命周期行为。
 */
@Getter
public class BankGuarantee extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_ISSUED = "ISSUED";
    public static final String STATUS_CLAIMED = "CLAIMED";
    public static final String STATUS_EXPIRED = "EXPIRED";

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

    /**
     * Issues a draft guarantee.
     * 开立草稿保函。
     */
    public void issue(LocalDate issueDate) {
        if (!STATUS_DRAFT.equals(guaranteeStatus)) {
            throw new BusinessException("只有草稿状态的保函才能开立");
        }
        this.guaranteeStatus = STATUS_ISSUED;
        this.issueDate = issueDate == null ? LocalDate.now() : issueDate;
    }

    /**
     * Marks the guarantee as claimed.
     * 标记保函已索赔。
     */
    public void claim() {
        if (!STATUS_ISSUED.equals(guaranteeStatus)) {
            throw new BusinessException("只有已开立的保函才能索赔");
        }
        this.guaranteeStatus = STATUS_CLAIMED;
    }

    /**
     * Marks the guarantee as expired.
     * 标记保函已到期。
     */
    public void expire() {
        if (!STATUS_ISSUED.equals(guaranteeStatus)) {
            throw new BusinessException("只有已开立的保函才能到期处理");
        }
        this.guaranteeStatus = STATUS_EXPIRED;
    }
}
