package com.forex.risk.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.risk.adapter.dto.CreateRiskLogReq;
import com.forex.risk.adapter.dto.RiskLogResp;
import com.forex.risk.adapter.dto.RiskPageQuery;
import com.forex.risk.adapter.dto.RiskReportResp;
import com.forex.risk.application.command.EvaluateCmd;
import com.forex.risk.application.command.GenerateReportCmd;
import jakarta.validation.Valid;
import com.forex.risk.application.service.RiskAppService;
import com.forex.risk.domain.model.aggregate.RiskMonitorLog;
import com.forex.risk.domain.model.entity.RiskReport;
import com.forex.risk.domain.model.query.RiskQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "风险监测")
@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskAppService riskAppService;

    @Operation(summary = "交易风险评估")
    @PostMapping("/evaluate")
    @RequirePermission("risk:evaluate")
    public R<RiskLogResp> evaluate(@Valid @RequestBody EvaluateCmd cmd) {
        RiskMonitorLog log = riskAppService.evaluateTransaction(cmd);
        return R.ok(toRiskLogResp(log));
    }

    @Operation(summary = "创建风险日志")
    @PostMapping("/log/create")
    @RequirePermission("risk:log")
    @Idempotent(key = "#req.customerId + '_' + #req.bizType + '_' + #req.bizNo + '_log'")
    public R<RiskLogResp> createLog(@Valid @RequestBody CreateRiskLogReq req) {
        RiskMonitorLog log = RiskMonitorLog.create(
                null, req.getCustomerId(), req.getBizType(), req.getBizNo(),
                req.getTransactionAmount(), req.getTransactionCurrency(), req.getTransactionTime(),
                req.getMonitorRuleCode(), req.getMonitorRuleName(), req.getRiskCategory(),
                req.getRiskLevel(), req.getRiskScore());
        RiskMonitorLog result = riskAppService.createRiskLog(log);
        return R.ok(toRiskLogResp(result));
    }

    @Operation(summary = "查询风险日志")
    @GetMapping("/log/{logNo}")
    public R<RiskLogResp> getLog(@PathVariable String logNo) {
        RiskMonitorLog log = riskAppService.getRiskLog(logNo);
        return R.ok(toRiskLogResp(log));
    }

    @Operation(summary = "分页查询风险日志")
    @PostMapping("/log/page")
    public R<PageResp<RiskLogResp>> pageQuery(@RequestBody RiskPageQuery req) {
        RiskQuery query = toRiskQuery(req);
        PageResp<RiskMonitorLog> page = riskAppService.pageQuery(query);
        List<RiskLogResp> respList = page.getRecords().stream()
                .map(this::toRiskLogResp)
                .toList();
        PageResp<RiskLogResp> result = PageResp.of(page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "生成风险报告")
    @PostMapping("/report/generate")
    @RequirePermission("risk:report")
    public R<RiskReportResp> generateReport(@Valid @RequestBody GenerateReportCmd cmd) {
        RiskReport report = riskAppService.generateReport(cmd);
        return R.ok("报告生成成功", toRiskReportResp(report));
    }

    @Operation(summary = "提交风险报告")
    @PostMapping("/report/submit/{reportNo}")
    @RequirePermission("risk:report")
    @RedisLock(key = "'risk:submit:' + #reportNo")
    public R<Void> submitReport(@PathVariable String reportNo) {
        riskAppService.submitReport(reportNo);
        return R.okMsg("报告提交成功");
    }

    @Operation(summary = "查询风险报告")
    @GetMapping("/report/{reportNo}")
    public R<RiskReportResp> getReport(@PathVariable String reportNo) {
        RiskReport report = riskAppService.getRiskReport(reportNo);
        return R.ok(toRiskReportResp(report));
    }

    private RiskQuery toRiskQuery(RiskPageQuery req) {
        RiskQuery query = new RiskQuery();
        query.setPageNum(req.getPageNum());
        query.setPageSize(req.getPageSize());
        query.setCustomerId(req.getCustomerId());
        query.setBizType(req.getBizType());
        query.setRiskCategory(req.getRiskCategory());
        query.setRiskLevel(req.getRiskLevel());
        query.setCheckResult(req.getCheckResult());
        query.setStartDate(req.getStartDate());
        query.setEndDate(req.getEndDate());
        return query;
    }

    private RiskLogResp toRiskLogResp(RiskMonitorLog log) {
        RiskLogResp resp = new RiskLogResp();
        resp.setId(log.getId());
        resp.setLogNo(log.getLogNo());
        resp.setCustomerId(log.getCustomerId());
        resp.setBizType(log.getBizType());
        resp.setBizNo(log.getBizNo());
        resp.setTransactionAmount(log.getTransactionAmount());
        resp.setTransactionCurrency(log.getTransactionCurrency());
        resp.setTransactionTime(log.getTransactionTime());
        resp.setMonitorRuleCode(log.getMonitorRuleCode());
        resp.setMonitorRuleName(log.getMonitorRuleName());
        resp.setRiskCategory(log.getRiskCategory());
        resp.setRiskLevel(log.getRiskLevel());
        resp.setRiskScore(log.getRiskScore());
        resp.setCheckResult(log.getCheckResult());
        resp.setOperatorId(log.getOperatorId());
        resp.setHandleTime(log.getHandleTime());
        resp.setHandleRemark(log.getHandleRemark());
        return resp;
    }

    private RiskReportResp toRiskReportResp(RiskReport report) {
        RiskReportResp resp = new RiskReportResp();
        resp.setId(report.getId());
        resp.setReportNo(report.getReportNo());
        resp.setReportType(report.getReportType());
        resp.setReportPeriod(report.getReportPeriod());
        resp.setCustomerId(report.getCustomerId());
        resp.setTotalTransactions(report.getTotalTransactions());
        resp.setTotalAmount(report.getTotalAmount());
        resp.setReportContent(report.getReportContent());
        resp.setReportStatus(report.getReportStatus());
        resp.setSubmitTime(report.getSubmitTime());
        resp.setSubmitterId(report.getSubmitterId());
        resp.setRegulatoryRef(report.getRegulatoryRef());
        return resp;
    }
}
