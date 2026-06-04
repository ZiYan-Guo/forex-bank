package com.forex.ai.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

@Getter
public class RiskAiAssessment extends BaseAggregate {

    private static final long serialVersionUID = 1L;

    public static final String AML = "AML";
    public static final String FRAUD = "FRAUD";
    public static final String SANCTION = "SANCTION";

    public static final String LEVEL_LOW = "LOW";
    public static final String LEVEL_MEDIUM = "MEDIUM";
    public static final String LEVEL_HIGH = "HIGH";
    public static final String LEVEL_PROHIBITED = "PROHIBITED";

    private Long id;
    private String assessmentId;
    private Long customerId;
    private String bizNo;
    private String riskType;
    private BigDecimal riskScore;
    private String riskLevel;
    private String aiAnalysis;
    private String recommendation;
    private String dataPointsJson;

    private RiskAiAssessment() {
        super();
    }

    public static RiskAiAssessment create(Long customerId, String bizNo, String riskType,
                                           BigDecimal riskScore, String riskLevel,
                                           String aiAnalysis, String recommendation,
                                           String dataPointsJson) {
        RiskAiAssessment assessment = new RiskAiAssessment();
        assessment.assessmentId = "RA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        assessment.customerId = customerId;
        assessment.bizNo = bizNo;
        assessment.riskType = riskType;
        assessment.riskScore = riskScore;
        assessment.riskLevel = riskLevel;
        assessment.aiAnalysis = aiAnalysis;
        assessment.recommendation = recommendation;
        assessment.dataPointsJson = dataPointsJson;
        assessment.validate();
        return assessment;
    }

    public static RiskAiAssessment reconstitute(Long id, String assessmentId, Long customerId,
                                                  String bizNo, String riskType,
                                                  BigDecimal riskScore, String riskLevel,
                                                  String aiAnalysis, String recommendation,
                                                  String dataPointsJson,
                                                  LocalDateTime createdAt, LocalDateTime updatedAt,
                                                  Integer version) {
        RiskAiAssessment assessment = new RiskAiAssessment();
        assessment.id = id;
        assessment.assessmentId = assessmentId;
        assessment.customerId = customerId;
        assessment.bizNo = bizNo;
        assessment.riskType = riskType;
        assessment.riskScore = riskScore;
        assessment.riskLevel = riskLevel;
        assessment.aiAnalysis = aiAnalysis;
        assessment.recommendation = recommendation;
        assessment.dataPointsJson = dataPointsJson;
        return assessment;
    }

    public boolean isEscalated() {
        return LEVEL_HIGH.equals(riskLevel) || LEVEL_PROHIBITED.equals(riskLevel);
    }

    public boolean isPassed(double threshold) {
        return riskScore != null && riskScore.doubleValue() <= threshold;
    }

    @Override
    protected void validate() {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId must not be null");
        }
        if (riskType == null || riskType.isBlank()) {
            throw new IllegalArgumentException("riskType must not be blank");
        }
        if (riskScore == null) {
            throw new IllegalArgumentException("riskScore must not be null");
        }
    }
}
