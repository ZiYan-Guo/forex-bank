package com.forex.reporting.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Capital account report persistent object.
 * 资本项目账户报告持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_capital_account_report")
public class CapitalAccountReportPO extends BasePO {
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
}
