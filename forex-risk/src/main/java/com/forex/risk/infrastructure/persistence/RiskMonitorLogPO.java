package com.forex.risk.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_risk_monitor_log")
public class RiskMonitorLogPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String logNo;
    private Long customerId;
    private String bizType;
    private String bizNo;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private LocalDateTime transactionTime;
    private String monitorRuleCode;
    private String monitorRuleName;
    private String riskCategory;
    private String riskLevel;
    private BigDecimal riskScore;
    private String checkResult;
    private Long operatorId;
    private LocalDateTime handleTime;
    private String handleRemark;
    private LocalDateTime createTime;
}
