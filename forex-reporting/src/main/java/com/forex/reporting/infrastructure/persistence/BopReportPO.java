package com.forex.reporting.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_balance_of_payment")
public class BopReportPO extends BasePO {

    private String reportNo;
    private String reportType;
    private Long customerId;
    private String customerName;
    private String transactionNo;
    private String transactionType;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private BigDecimal cnyAmount;
    private BigDecimal exchangeRate;
    private LocalDate transactionDate;
    private LocalDate settlementDate;
    private String bopCode;
    private String bopName;
    private String purposeCode;
    private String purposeRemark;
    private String counterpartyCountry;
    private String counterpartyName;
    private String reportStatus;
    private LocalDateTime submitTime;
    private String regulatoryRef;
    private String errorMsg;
}
