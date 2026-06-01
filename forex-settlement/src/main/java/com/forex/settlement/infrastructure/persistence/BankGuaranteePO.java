package com.forex.settlement.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_bank_guarantee")
public class BankGuaranteePO extends BasePO {

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
}
