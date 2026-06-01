package com.forex.payment.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_cross_border_payment")
public class CrossBorderPaymentPO extends BasePO {

    private String paymentNo;
    private Long customerId;
    private String paymentDirection;
    private String paymentType;
    private BigDecimal payAmount;
    private String payCurrency;
    private BigDecimal settlementAmount;
    private BigDecimal exchangeRate;
    private String senderInfo;
    private String beneficiaryInfo;
    private String intermediaryBankInfo;
    private String payingBankCode;
    private String receivingBankCode;
    private String messageType;
    private String swiftRef;
    private String cipsRef;
    private String gpiTrackingId;
    private String gpiStatus;
    private String paymentPurpose;
    private String bankPurposeCode;
    private String chargeBearer;
    private BigDecimal feeAmount;
    private BigDecimal telegraphicFee;
    private BigDecimal commissionAmount;
    private String paymentStatus;
    private LocalDateTime submitTime;
    private LocalDate valueDate;
    private LocalDate settlementDate;
    private Long operatorId;
    private Long approverId;
    private String remark;
}
