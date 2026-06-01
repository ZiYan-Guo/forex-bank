package com.forex.risk.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_risk_report")
public class RiskReportPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String reportNo;
    private String reportType;
    private String reportPeriod;
    private Long customerId;
    private Integer totalTransactions;
    private BigDecimal totalAmount;
    private String reportContent;
    private String reportStatus;
    private LocalDateTime submitTime;
    private Long submitterId;
    private String regulatoryRef;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
