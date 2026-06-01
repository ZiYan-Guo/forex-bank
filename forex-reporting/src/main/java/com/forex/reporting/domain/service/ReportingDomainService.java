package com.forex.reporting.domain.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.reporting.domain.event.ReportSubmittedEvent;
import com.forex.reporting.domain.model.aggregate.BopReport;
import com.forex.reporting.domain.model.entity.CapitalAccountReport;
import com.forex.reporting.domain.model.entity.ForexSettlementReport;
import com.forex.reporting.domain.repository.BopReportRepository;
import com.forex.reporting.domain.repository.CapitalReportRepository;
import com.forex.reporting.domain.repository.SettlementReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportingDomainService {

    private final BopReportRepository bopReportRepository;
    private final SettlementReportRepository settlementReportRepository;
    private final CapitalReportRepository capitalReportRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BopReport createBopReport(BopReport report) {
        BopReport saved = bopReportRepository.save(report);
        log.info("Created BOP report: {}", saved.getReportNo());
        return saved;
    }

    public ForexSettlementReport createSettlementReport(ForexSettlementReport report) {
        ForexSettlementReport saved = settlementReportRepository.save(report);
        log.info("Created settlement report: {}", saved.getReportNo());
        return saved;
    }

    public CapitalAccountReport createCapitalReport(CapitalAccountReport report) {
        CapitalAccountReport saved = capitalReportRepository.save(report);
        log.info("Created capital account report: {}", saved.getReportNo());
        return saved;
    }

    public void submitReportBatch(List<BopReport> reports) {
        if (reports == null || reports.isEmpty()) {
            throw new BusinessException("申报列表不能为空");
        }
        for (BopReport report : reports) {
            report.submit();
            bopReportRepository.save(report);
            eventPublisher.publishEvent(new ReportSubmittedEvent(
                    report.getId(), report.getReportNo(), report.getReportType()));
        }
        log.info("Submitted batch reports, count: {}", reports.size());
    }

    public void markAsSubmitted(BopReport report) {
        report.submit();
        bopReportRepository.save(report);
        eventPublisher.publishEvent(new ReportSubmittedEvent(
                report.getId(), report.getReportNo(), report.getReportType()));
        log.info("Marked report as submitted: {}", report.getReportNo());
    }

    public void markAsAccepted(BopReport report, String ref) {
        report.accept();
        bopReportRepository.save(report);
        log.info("Marked report as accepted: {}, ref: {}", report.getReportNo(), ref);
    }

    public void markAsRejected(BopReport report, String reason) {
        report.reject(reason);
        bopReportRepository.save(report);
        log.info("Marked report as rejected: {}, reason: {}", report.getReportNo(), reason);
    }
}
