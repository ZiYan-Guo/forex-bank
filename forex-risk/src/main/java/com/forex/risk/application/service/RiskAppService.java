package com.forex.risk.application.service;

import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.risk.application.command.EvaluateCmd;
import com.forex.risk.application.command.GenerateReportCmd;
import com.forex.risk.domain.model.aggregate.RiskMonitorLog;
import com.forex.risk.domain.model.entity.RiskReport;
import com.forex.risk.domain.model.query.RiskQuery;
import com.forex.risk.domain.repository.RiskMonitorLogRepository;
import com.forex.risk.domain.repository.RiskReportRepository;
import com.forex.risk.domain.service.RiskDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RiskAppService {

    private final RiskDomainService riskDomainService;
    private final RiskMonitorLogRepository riskMonitorLogRepository;
    private final RiskReportRepository riskReportRepository;

    public RiskMonitorLog evaluateTransaction(EvaluateCmd cmd) {
        String logNo = "RML" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        RiskMonitorLog log = RiskMonitorLog.create(
                logNo, cmd.getCustomerId(), cmd.getBizType(),
                cmd.getBizNo(), cmd.getTransactionAmount(),
                cmd.getTransactionCurrency(), cmd.getTransactionTime(),
                null, null, null, null, null);
        riskDomainService.evaluateTransaction(log);
        return riskMonitorLogRepository.findByLogNo(logNo).orElse(log);
    }

    public RiskMonitorLog createRiskLog(RiskMonitorLog log) {
        return riskDomainService.createRiskLog(log);
    }

    public RiskMonitorLog getRiskLog(String logNo) {
        return riskMonitorLogRepository.findByLogNo(logNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "风险日志不存在"));
    }

    public PageResp<RiskMonitorLog> pageQuery(RiskQuery query) {
        return riskMonitorLogRepository.pageQuery(query);
    }

    public RiskReport generateReport(GenerateReportCmd cmd) {
        return riskDomainService.generateRiskReport(cmd.getReportType(),
                cmd.getReportPeriod(), cmd.getCustomerId());
    }

    public RiskReport getRiskReport(String reportNo) {
        return riskReportRepository.findByReportNo(reportNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "风险报告不存在"));
    }

    @RedisLock(key = "'risk:submit:' + #reportNo")
    public void submitReport(String reportNo) {
        RiskReport report = riskReportRepository.findByReportNo(reportNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "风险报告不存在"));
        riskDomainService.submitReport(report);
    }
}
