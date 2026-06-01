package com.forex.risk.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class RiskMonitorLog extends BaseAggregate {

    private static final String CHECK_RESULT_PASS = "PASS";
    private static final String CHECK_RESULT_REJECT = "REJECT";
    private static final String CHECK_RESULT_MANUAL_REVIEW = "MANUAL_REVIEW";
    private static final String CHECK_RESULT_ESCALATED = "ESCALATED";

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

    private RiskMonitorLog() {
        super();
    }

    public static RiskMonitorLog create(String logNo, Long customerId, String bizType,
                                         String bizNo, BigDecimal transactionAmount,
                                         String transactionCurrency, LocalDateTime transactionTime,
                                         String monitorRuleCode, String monitorRuleName,
                                         String riskCategory, String riskLevel,
                                         BigDecimal riskScore) {
        RiskMonitorLog log = new RiskMonitorLog();
        log.logNo = logNo;
        log.customerId = customerId;
        log.bizType = bizType;
        log.bizNo = bizNo;
        log.transactionAmount = transactionAmount;
        log.transactionCurrency = transactionCurrency;
        log.transactionTime = transactionTime;
        log.monitorRuleCode = monitorRuleCode;
        log.monitorRuleName = monitorRuleName;
        log.riskCategory = riskCategory;
        log.riskLevel = riskLevel;
        log.riskScore = riskScore;
        log.checkResult = "PENDING";
        log.validate();
        return log;
    }

    public static RiskMonitorLog reconstitute(Long id, String logNo, Long customerId,
                                               String bizType, String bizNo,
                                               BigDecimal transactionAmount, String transactionCurrency,
                                               LocalDateTime transactionTime, String monitorRuleCode,
                                               String monitorRuleName, String riskCategory,
                                               String riskLevel, BigDecimal riskScore,
                                               String checkResult, Long operatorId,
                                               LocalDateTime handleTime, String handleRemark) {
        RiskMonitorLog log = new RiskMonitorLog();
        log.id = id;
        log.logNo = logNo;
        log.customerId = customerId;
        log.bizType = bizType;
        log.bizNo = bizNo;
        log.transactionAmount = transactionAmount;
        log.transactionCurrency = transactionCurrency;
        log.transactionTime = transactionTime;
        log.monitorRuleCode = monitorRuleCode;
        log.monitorRuleName = monitorRuleName;
        log.riskCategory = riskCategory;
        log.riskLevel = riskLevel;
        log.riskScore = riskScore;
        log.checkResult = checkResult;
        log.operatorId = operatorId;
        log.handleTime = handleTime;
        log.handleRemark = handleRemark;
        return log;
    }

    public void setEvaluateResult(String result) {
        this.checkResult = result;
    }

    public void pass() {
        this.checkResult = CHECK_RESULT_PASS;
        this.handleTime = LocalDateTime.now();
        markUpdated();
    }

    public void reject() {
        this.checkResult = CHECK_RESULT_REJECT;
        this.handleTime = LocalDateTime.now();
        markUpdated();
    }

    public void escalate() {
        this.checkResult = CHECK_RESULT_ESCALATED;
        this.handleTime = LocalDateTime.now();
        markUpdated();
    }

    public void manualReview() {
        this.checkResult = CHECK_RESULT_MANUAL_REVIEW;
        this.handleTime = LocalDateTime.now();
        markUpdated();
    }

    public void triggeredBy(String ruleCode, String ruleName) {
        this.monitorRuleCode = ruleCode;
        this.monitorRuleName = ruleName;
        markUpdated();
    }

    public static RiskMonitorLog triggered(String logNo, Long customerId, String bizType,
                                            String bizNo, BigDecimal transactionAmount,
                                            String transactionCurrency, LocalDateTime transactionTime,
                                            String monitorRuleCode, String monitorRuleName,
                                            String riskCategory, String riskLevel,
                                            BigDecimal riskScore) {
        RiskMonitorLog log = create(logNo, customerId, bizType, bizNo,
                transactionAmount, transactionCurrency, transactionTime,
                monitorRuleCode, monitorRuleName, riskCategory, riskLevel, riskScore);
        return log;
    }

    @Override
    protected void validate() {
        if (logNo == null || logNo.isBlank()) {
            throw new BusinessException("监控日志编号不能为空");
        }
        if (customerId == null) {
            throw new BusinessException("客户ID不能为空");
        }
        if (bizType == null || bizType.isBlank()) {
            throw new BusinessException("业务类型不能为空");
        }
        if (bizNo == null || bizNo.isBlank()) {
            throw new BusinessException("业务编号不能为空");
        }
        if (riskCategory == null || riskCategory.isBlank()) {
            throw new BusinessException("风险类别不能为空");
        }
        if (riskLevel == null || riskLevel.isBlank()) {
            throw new BusinessException("风险级别不能为空");
        }
    }
}
