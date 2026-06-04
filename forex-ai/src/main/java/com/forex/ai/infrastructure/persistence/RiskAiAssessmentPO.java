package com.forex.ai.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_risk_ai_assessment")
public class RiskAiAssessmentPO extends BasePO {

    private String assessmentId;
    private Long customerId;
    private String bizNo;
    private String riskType;
    private BigDecimal riskScore;
    private String riskLevel;
    private String aiAnalysis;
    private String recommendation;
    private String dataPointsJson;
}
