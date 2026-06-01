package com.forex.risk.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.risk.adapter.dto.RiskLogResp;
import com.forex.risk.adapter.dto.RiskReportResp;
import com.forex.risk.application.command.EvaluateCmd;
import com.forex.risk.application.command.GenerateReportCmd;
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
    public R<RiskLogResp> evaluate(@RequestBody EvaluateCmd cmd) {
        RiskMonitorLog log = riskAppService.evaluateTransaction(cmd);
        return R.ok(toRiskLogResp(log));
    }

    @Operation(summary = "创建风险日志")
    @PostMapping("/log/create")
    @Idempotent(key = "#log.customerId + '_' + #log.bizType + '_' + #log.bizNo + '_log'")
    public R<RiskLogResp> createLog(@RequestBody RiskMonitorLog log) {
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
    public R<PageResp<RiskLogResp>> pageQuery(@RequestBody RiskQuery query) {
        PageResp<RiskMonitorLog> page = riskAppService.pageQuery(query);
        List<RiskLogResp> respList = page.getRecords().stream()
                .map(this::toRiskLogResp)
                .toList();
        PageResp<RiskLogResp> result = PageResp.of(page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "生成风险报告")
    @PostMapping("/report/generate")
    public R<RiskReportResp> generateReport(@RequestBody GenerateReportCmd cmd) {
        RiskReport report = riskAppService.generateReport(cmd);
        return R.ok("报告生成成功", toRiskReportResp(report));
    }

    @Operation(summary = "提交风险报告")
    @PostMapping("/report/submit/{reportNo}")
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
