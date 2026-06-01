package com.forex.settlement.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_letter_of_credit")
public class LetterOfCreditPO extends BasePO {

    private String lcNo;
    private Long customerId;
    private String lcType;
    private String lcDirection;
    private BigDecimal lcAmount;
    private String lcCurrency;
    private BigDecimal tolerancePct;
    private String applicantInfo;
    private String beneficiaryInfo;
    private String issuingBankInfo;
    private String advisingBankInfo;
    private String confirmingBankInfo;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String expiryPlace;
    private LocalDate latestShipDate;
    private Integer presentationPeriod;
    private String availableWith;
    private String availableBy;
    private String draftTenor;
    private String partialShipment;
    private String transshipment;
    private String portOfLoading;
    private String portOfDischarge;
    private String goodsDescription;
    private String documentsRequired;
    private String additionalConditions;
    private String confirmationInstruction;
    private String chargeBearer;
    private String lcStatus;
    private String swiftRef;
    private BigDecimal marginPct;
    private BigDecimal marginAmount;
    private BigDecimal feeAmount;
    private Long operatorId;
    private String remark;
}
