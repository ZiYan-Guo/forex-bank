package com.forex.cashpool.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Overseas lending persistent object.
 * 境外放款合同持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_overseas_lending")
public class OverseasLendingPO extends BasePO {

    private String contractNo;
    private Long customerId;
    private BigDecimal loanAmount;
    private String loanCurrency;
    private BigDecimal interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String repaymentMethod;
    private String loanStatus;
    private BigDecimal outstandingPrincipal;
    private BigDecimal totalInterest;
    private Long poolId;
}
