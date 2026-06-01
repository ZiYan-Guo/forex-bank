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
@TableName("t_forex_settlement_report")
public class SettlementReportPO extends BasePO {

    private String reportNo;
    private Long customerId;
    private String exchangeOrderNo;
    private String exchangeType;
    private String dealType;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private BigDecimal cnyAmount;
    private BigDecimal exchangeRate;
    private LocalDate transactionDate;
    private LocalDate settleDate;
    private String settlementCode;
    private String reportStatus;
    private LocalDateTime submitTime;
    private String regulatoryRef;
}
