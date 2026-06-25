package com.forex.reporting.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.reporting.adapter.dto.BopReportResp;
import com.forex.reporting.adapter.dto.SettlementReportResp;
import com.forex.reporting.application.command.BopReportCmd;
import com.forex.reporting.application.command.CapitalReportCmd;
import com.forex.reporting.application.command.SettlementReportCmd;
import com.forex.reporting.application.service.ReportingAppService;
import com.forex.reporting.domain.model.aggregate.BopReport;
import com.forex.reporting.domain.model.entity.CapitalAccountReport;
import com.forex.reporting.domain.model.entity.ForexSettlementReport;
import com.forex.reporting.domain.model.query.ReportQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "监管报送")
@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingAppService reportingAppService;

    @Operation(summary = "创建国际收支申报")
    @RequirePermission("reporting:create")
    @PostMapping("/bop/create")
    @Idempotent(key = "#cmd.transactionNo + '_bop'")
    public R<BopReportResp> createBopReport(@RequestBody BopReportCmd cmd) {
        BopReport report = reportingAppService.createBopReport(cmd);
        return R.ok("创建成功", toBopReportResp(report));
    }

    @Operation(summary = "创建结售汇统计申报")
    @RequirePermission("reporting:create")
    @PostMapping("/settlement/create")
    @Idempotent(key = "#cmd.exchangeOrderNo + '_settlement'")
    public R<SettlementReportResp> createSettlementReport(@RequestBody SettlementReportCmd cmd) {
        ForexSettlementReport report = reportingAppService.createSettlementReport(cmd);
        return R.ok("创建成功", toSettlementReportResp(report));
    }

    @Operation(summary = "创建资本项目申报")
    @RequirePermission("reporting:create")
    @PostMapping("/capital/create")
    @Idempotent(key = "#cmd.accountNo + '_' + #cmd.transactionType + '_capital'")
    public R<Void> createCapitalReport(@RequestBody CapitalReportCmd cmd) {
        reportingAppService.createCapitalReport(cmd);
        return R.okMsg("创建成功");
    }

    @Operation(summary = "批量提交申报")
    @RequirePermission("reporting:submit")
    @PostMapping("/batch/submit")
    @RedisLock(key = "'reporting:batch:submit:' + #reportType")
    public R<Void> submitBatch(@RequestParam String reportType, @RequestBody List<String> reportNos) {
        reportingAppService.submitBatch(reportType, reportNos);
        return R.okMsg("批量提交成功");
    }

    @Operation(summary = "查询BOP申报详情")
    @GetMapping("/bop/{reportNo}")
    public R<BopReportResp> getBopReport(@PathVariable String reportNo) {
        BopReport report = reportingAppService.getBopReport(reportNo);
        return R.ok(toBopReportResp(report));
    }

    @Operation(summary = "分页查询BOP申报")
    @RequirePermission("reporting:page")
    @PostMapping("/bop/page")
    public R<PageResp<BopReportResp>> pageQuery(@RequestBody ReportQuery query) {
        PageResp<BopReport> page = reportingAppService.pageQuery(query);
        List<BopReportResp> respList = page.getRecords().stream()
                .map(this::toBopReportResp)
                .toList();
        PageResp<BopReportResp> result = PageResp.of(page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "导出申报报表")
    @GetMapping("/export/{reportNo}")
    public R<byte[]> exportReport(@PathVariable String reportNo) {
        byte[] data = reportingAppService.exportReport(reportNo);
        return R.ok(data);
    }

    private BopReportResp toBopReportResp(BopReport report) {
        BopReportResp resp = new BopReportResp();
        resp.setId(report.getId());
        resp.setReportNo(report.getReportNo());
        resp.setReportType(report.getReportType());
        resp.setCustomerId(report.getCustomerId());
        resp.setCustomerName(report.getCustomerName());
        resp.setTransactionNo(report.getTransactionNo());
        resp.setTransactionType(report.getTransactionType());
        resp.setTransactionAmount(report.getTransactionAmount());
        resp.setTransactionCurrency(report.getTransactionCurrency());
        resp.setCnyAmount(report.getCnyAmount());
        resp.setExchangeRate(report.getExchangeRate());
        resp.setTransactionDate(report.getTransactionDate());
        resp.setSettlementDate(report.getSettlementDate());
        resp.setBopCode(report.getBopCode());
        resp.setBopName(report.getBopName());
        resp.setPurposeCode(report.getPurposeCode());
        resp.setPurposeRemark(report.getPurposeRemark());
        resp.setCounterpartyCountry(report.getCounterpartyCountry());
        resp.setCounterpartyName(report.getCounterpartyName());
        resp.setReportStatus(report.getReportStatus());
        resp.setSubmitTime(report.getSubmitTime());
        resp.setRegulatoryRef(report.getRegulatoryRef());
        resp.setErrorMsg(report.getErrorMsg());
        return resp;
    }

    private SettlementReportResp toSettlementReportResp(ForexSettlementReport report) {
        SettlementReportResp resp = new SettlementReportResp();
        resp.setId(report.getId());
        resp.setReportNo(report.getReportNo());
        resp.setCustomerId(report.getCustomerId());
        resp.setExchangeOrderNo(report.getExchangeOrderNo());
        resp.setExchangeType(report.getExchangeType());
        resp.setDealType(report.getDealType());
        resp.setTransactionAmount(report.getTransactionAmount());
        resp.setTransactionCurrency(report.getTransactionCurrency());
        resp.setCnyAmount(report.getCnyAmount());
        resp.setExchangeRate(report.getExchangeRate());
        resp.setTransactionDate(report.getTransactionDate());
        resp.setSettleDate(report.getSettleDate());
        resp.setSettlementCode(report.getSettlementCode());
        resp.setReportStatus(report.getReportStatus());
        resp.setSubmitTime(report.getSubmitTime());
        resp.setRegulatoryRef(report.getRegulatoryRef());
        return resp;
    }
}
