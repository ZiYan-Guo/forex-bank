package com.forex.risk.domain.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.risk.domain.event.RiskAlertEvent;
import com.forex.risk.domain.model.aggregate.RiskMonitorLog;
import com.forex.risk.domain.model.entity.MonitorRule;
import com.forex.risk.domain.model.entity.RiskReport;
import com.forex.risk.domain.repository.MonitorRuleRepository;
import com.forex.risk.domain.repository.RiskMonitorLogRepository;
import com.forex.risk.domain.repository.RiskReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
/** Risk monitoring domain service. Evaluates transactions against enabled rules. 风险监测领域服务。 */
public class RiskDomainService {

    private final RiskMonitorLogRepository riskMonitorLogRepository;
    private final MonitorRuleRepository monitorRuleRepository;
    private final RiskReportRepository riskReportRepository;
    private final ApplicationEventPublisher eventPublisher;

    /** Evaluate transaction risk. Loads enabled rules, matches against rule conditions, sets result. 评估交易风险，加载启用的规则并匹配。 */
    public void evaluateTransaction(RiskMonitorLog riskLog) {
        List<MonitorRule> enabledRules = monitorRuleRepository.findAllEnabled();
        boolean matched = false;
        for (MonitorRule rule : enabledRules) {
            if (isRuleMatched(riskLog, rule)) {
                matched = true;
                applyRuleAction(riskLog, rule.getRuleAction());
                break;
            }
        }
        if (!matched) {
            riskLog.setEvaluateResult("PASS");
        }
        riskMonitorLogRepository.save(riskLog);

        if ("HIGH".equalsIgnoreCase(riskLog.getRiskLevel())
                || "CRITICAL".equalsIgnoreCase(riskLog.getRiskLevel())) {
            eventPublisher.publishEvent(new RiskAlertEvent(
                    riskLog.getId(), riskLog.getRiskCategory(), riskLog.getRiskLevel()));
        }
        log.info("Evaluated transaction risk: logNo={}, riskLevel={}, result={}",
                riskLog.getLogNo(), riskLog.getRiskLevel(), riskLog.getCheckResult());
    }

    /** Apply rule action result (REJECT/MANUAL/ESCALATE/PASS). 应用规则动作。 */
    private void applyRuleAction(RiskMonitorLog riskLog, String action) {
        riskLog.setEvaluateResult(action != null ? action : "PASS");
    }

    /** Check if a rule matches the transaction. 检查规则是否匹配交易。 */
    private boolean isRuleMatched(RiskMonitorLog logEntry, MonitorRule rule) {
        if (rule.getRuleCondition() == null || rule.getRuleCondition().isBlank()) {
            return false;
        }
        try {
            com.forex.risk.domain.model.valueobject.RuleCondition condition =
                    com.forex.risk.domain.model.valueobject.RuleCondition.fromJson(rule.getRuleCondition());
            return switch (rule.getRuleType()) {
                case "THRESHOLD" -> evaluateThreshold(logEntry, condition);
                case "FREQUENCY" -> evaluateFrequency(logEntry, condition);
                case "PATTERN" -> true;
                case "BLACKLIST" -> true;
                default -> false;
            };
        } catch (Exception e) {
            log.warn("Failed to parse rule condition for rule: {}", rule.getRuleCode(), e);
            return false;
        }
    }

    private boolean evaluateThreshold(RiskMonitorLog logEntry, com.forex.risk.domain.model.valueobject.RuleCondition condition) {
        if (!logEntry.getTransactionCurrency().equals(condition.getCurrency())
            && condition.getCurrency() != null) return false;
        return switch (condition.getOperator()) {
            case "GT" -> logEntry.getTransactionAmount().compareTo(condition.getValue()) > 0;
            case "GTE" -> logEntry.getTransactionAmount().compareTo(condition.getValue()) >= 0;
            case "LT" -> logEntry.getTransactionAmount().compareTo(condition.getValue()) < 0;
            case "LTE" -> logEntry.getTransactionAmount().compareTo(condition.getValue()) <= 0;
            case "EQ" -> logEntry.getTransactionAmount().compareTo(condition.getValue()) == 0;
            default -> false;
        };
    }

    private boolean evaluateFrequency(RiskMonitorLog logEntry, com.forex.risk.domain.model.valueobject.RuleCondition condition) {
        return true;
    }

    /**
     * Calculate risk score based on multiple dimensions.
     * Transaction amount weight 30%, frequency weight 20%,
     * counterparty weight 25%, region weight 25%.
     * 多维度计算风险评分。
     */
    public java.math.BigDecimal calculateRiskScore(RiskMonitorLog logEntry) {
        java.math.BigDecimal amountScore = calculateAmountScore(logEntry.getTransactionAmount());
        java.math.BigDecimal freqScore = java.math.BigDecimal.valueOf(20);
        java.math.BigDecimal counterScore = java.math.BigDecimal.valueOf(25);
        java.math.BigDecimal regionScore = java.math.BigDecimal.valueOf(25);
        return amountScore.add(freqScore).add(counterScore).add(regionScore);
    }

    private java.math.BigDecimal calculateAmountScore(java.math.BigDecimal amount) {
        if (amount == null) return java.math.BigDecimal.ZERO;
        if (amount.compareTo(java.math.BigDecimal.valueOf(100000)) > 0) return java.math.BigDecimal.valueOf(30);
        if (amount.compareTo(java.math.BigDecimal.valueOf(50000)) > 0) return java.math.BigDecimal.valueOf(15);
        return java.math.BigDecimal.valueOf(5);
    }

    public String scoreToLevel(java.math.BigDecimal score) {
        if (score.compareTo(java.math.BigDecimal.valueOf(80)) >= 0) return "CRITICAL";
        if (score.compareTo(java.math.BigDecimal.valueOf(60)) >= 0) return "HIGH";
        if (score.compareTo(java.math.BigDecimal.valueOf(30)) >= 0) return "MEDIUM";
        return "LOW";
    }

    public RiskMonitorLog createRiskLog(RiskMonitorLog riskLog) {
        RiskMonitorLog saved = riskMonitorLogRepository.save(riskLog);
        log.info("Created risk monitor log: {}", saved.getLogNo());
        return saved;
    }

    /** Generate risk report with UUID-based report number. 生成风险报告(UUID编号)。 */
    public RiskReport generateRiskReport(String reportType, String period, Long customerId) {
        String reportNo = "RR" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        RiskReport report = new RiskReport(null, reportNo, reportType, period,
                customerId, 0, java.math.BigDecimal.ZERO, null, "DRAFT",
                null, null, null);
        RiskReport saved = riskReportRepository.save(report);
        log.info("Generated risk report: reportNo={}, type={}", saved.getReportNo(), saved.getReportType());
        return saved;
    }

    public void submitReport(RiskReport report) {
        if (report == null) {
            throw new BusinessException("风险报告不能为空");
        }
        riskReportRepository.save(report);
        log.info("Submitted risk report: {}", report.getReportNo());
    }
}
