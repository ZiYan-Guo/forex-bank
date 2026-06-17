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
@TableName("t_payment_reconciliation")
public class PaymentReconciliationPO extends BasePO {

    private Long paymentId;
    private String nostroAccount;
    private String currency;
    private String transactionRef;
    private LocalDate statementDate;
    private BigDecimal nostroAmount;
    private String nostroDirection;
    private BigDecimal systemAmount;
    private String systemDirection;
    private String reconciliationStatus;
    private LocalDateTime matchTime;
    private BigDecimal difference;
}
