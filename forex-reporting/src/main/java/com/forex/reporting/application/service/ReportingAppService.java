package com.forex.reporting.application.service;

import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.reporting.application.command.BopReportCmd;
import com.forex.reporting.application.command.CapitalReportCmd;
import com.forex.reporting.application.command.SettlementReportCmd;
import com.forex.reporting.domain.model.aggregate.BopReport;
import com.forex.reporting.domain.model.entity.CapitalAccountReport;
import com.forex.reporting.domain.model.entity.ForexSettlementReport;
import com.forex.reporting.domain.model.query.ReportQuery;
import com.forex.reporting.domain.repository.BopReportRepository;
import com.forex.reporting.domain.repository.CapitalReportRepository;
import com.forex.reporting.domain.repository.SettlementReportRepository;
import com.forex.reporting.domain.service.ReportingDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportingAppService {

    private final ReportingDomainService reportingDomainService;
    private final BopReportRepository bopReportRepository;
    private final SettlementReportRepository settlementReportRepository;
    private final CapitalReportRepository capitalReportRepository;

    public BopReport createBopReport(BopReportCmd cmd) {
        String reportNo = "BOP" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        BopReport report = BopReport.create(
                reportNo, "BOP", cmd.getCustomerId(),
                cmd.getCustomerName(), cmd.getTransactionNo(),
                cmd.getTransactionType(), cmd.getTransactionAmount(),
                cmd.getTransactionCurrency(), cmd.getCnyAmount(),
                cmd.getExchangeRate(), cmd.getTransactionDate(),
                cmd.getSettlementDate(), cmd.getBopCode(), cmd.getBopName(),
                null, null,
                cmd.getCounterpartyCountry(), null);
        return reportingDomainService.createBopReport(report);
    }

    public ForexSettlementReport createSettlementReport(SettlementReportCmd cmd) {
        String reportNo = "SLR" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        ForexSettlementReport report = new ForexSettlementReport(
                null, reportNo, cmd.getCustomerId(),
                cmd.getExchangeOrderNo(), cmd.getExchangeType(),
                cmd.getDealType(), cmd.getTransactionAmount(),
                cmd.getTransactionCurrency(), cmd.getCnyAmount(),
                cmd.getExchangeRate(), cmd.getTransactionDate(),
                null, cmd.getSettlementCode(),
                "DRAFT", null, null);
        return reportingDomainService.createSettlementReport(report);
    }

    public CapitalAccountReport createCapitalReport(CapitalReportCmd cmd) {
        String reportNo = "CAR" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        CapitalAccountReport report = new CapitalAccountReport(
                null, reportNo, cmd.getCustomerId(),
                cmd.getAccountNo(), cmd.getReportType(),
                cmd.getTransactionType(), cmd.getTransactionAmount(),
                cmd.getTransactionCurrency(), cmd.getTransactionDate(),
                cmd.getCapitalCode(), "DRAFT", null, null);
        return reportingDomainService.createCapitalReport(report);
    }

    @RedisLock(key = "'reporting:batch:submit:' + #reportType")
    public void submitBatch(String reportType, List<String> reportNos) {
        for (String reportNo : reportNos) {
            BopReport report = bopReportRepository.findByReportNo(reportNo)
                    .orElseThrow(() -> new IllegalArgumentException("申报报告不存在: " + reportNo));
            reportingDomainService.markAsSubmitted(report);
        }
    }

    public BopReport getBopReport(String reportNo) {
        return bopReportRepository.findByReportNo(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("BOP申报不存在"));
    }

    public PageResp<BopReport> pageQuery(ReportQuery query) {
        return bopReportRepository.pageQuery(query);
    }

    public byte[] exportReport(String reportNo) {
        return new byte[0];
    }
}
