package com.forex.reporting.domain.service;

import com.forex.reporting.domain.event.ReportSubmittedEvent;
import com.forex.reporting.domain.model.aggregate.BopReport;
import com.forex.reporting.domain.model.entity.CapitalAccountReport;
import com.forex.reporting.domain.model.entity.ForexSettlementReport;
import com.forex.reporting.domain.repository.BopReportRepository;
import com.forex.reporting.domain.repository.CapitalReportRepository;
import com.forex.reporting.domain.repository.SettlementReportRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportingDomainServiceTest {

    @Mock private BopReportRepository bopReportRepository;
    @Mock private SettlementReportRepository settlementReportRepository;
    @Mock private CapitalReportRepository capitalReportRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Captor private ArgumentCaptor<ReportSubmittedEvent> submittedCaptor;

    private ReportingDomainService reportingDomainService;

    @BeforeEach
    void setUp() {
        reportingDomainService = new ReportingDomainService(
                bopReportRepository, settlementReportRepository,
                capitalReportRepository, eventPublisher);
    }

    private BopReport createDraftBopReport() {
        return BopReport.create("BOP2026060100001", "BOP_PAYMENT", 1001L,
                "CUSTOMER CO", "TXN001", "OUTWARD_PAYMENT",
                new BigDecimal("10000.00"), "USD", new BigDecimal("72400.00"),
                new BigDecimal("7.2400"), LocalDate.now(), LocalDate.now(),
                "121010", "货物贸易-一般贸易", "P0101", "进口付汇",
                "US", "TRADING PARTNER");
    }

    @Test
    @DisplayName("Create BOP report saves and returns report")
    void testCreateBopReport() {
        BopReport report = createDraftBopReport();
        when(bopReportRepository.save(any())).thenReturn(report);
        BopReport result = reportingDomainService.createBopReport(report);
        assertNotNull(result);
        assertEquals("BOP2026060100001", result.getReportNo());
    }

    @Test
    @DisplayName("Create settlement report saves and returns")
    void testCreateSettlementReport() {
        ForexSettlementReport r = new ForexSettlementReport(null, "FS20260601", 1001L,
                "EX001", "SPOT", "BUY", new BigDecimal("10000.00"), "USD",
                new BigDecimal("72400.00"), new BigDecimal("7.24"),
                LocalDate.now(), LocalDate.now(), "S1001", "DRAFT", null, null);
        when(settlementReportRepository.save(any())).thenReturn(r);
        ForexSettlementReport result = reportingDomainService.createSettlementReport(r);
        assertNotNull(result);
        verify(settlementReportRepository).save(r);
    }

    @Test
    @DisplayName("Create capital report saves and returns")
    void testCreateCapitalReport() {
        CapitalAccountReport r = new CapitalAccountReport(null, "CA20260601", 1001L,
                "ACC001", "INWARD", "CAPITAL", new BigDecimal("500000.00"), "USD",
                LocalDate.now(), "C101", "DRAFT", null, null);
        when(capitalReportRepository.save(any())).thenReturn(r);
        CapitalAccountReport result = reportingDomainService.createCapitalReport(r);
        assertNotNull(result);
        verify(capitalReportRepository).save(r);
    }

    @Test
    @DisplayName("Submit batch reports fires events for each")
    void testSubmitReportBatch() {
        BopReport r1 = createDraftBopReport();
        BopReport r2 = BopReport.create("BOP2026060100002", "BOP_PAYMENT", 1002L,
                "CUST2", "TXN002", "OUTWARD_PAYMENT", new BigDecimal("5000.00"), "EUR",
                new BigDecimal("40000.00"), new BigDecimal("8.0000"),
                LocalDate.now(), LocalDate.now(), "121010", "货物贸易", "P0101",
                "进口付汇", "DE", "PARTNER2");
        when(bopReportRepository.save(any())).thenReturn(r1, r2);

        reportingDomainService.submitReportBatch(List.of(r1, r2));

        assertEquals("SUBMITTED", r1.getReportStatus());
        assertEquals("SUBMITTED", r2.getReportStatus());
        verify(eventPublisher, times(2)).publishEvent(any(ReportSubmittedEvent.class));
    }

    @Test
    @DisplayName("Submit report batch throws for empty list")
    void testSubmitReportBatch_Empty() {
        assertThrows(RuntimeException.class,
                () -> reportingDomainService.submitReportBatch(List.of()));
    }

    @Test
    @DisplayName("Mark as submitted transitions to SUBMITTED")
    void testMarkAsSubmitted() {
        BopReport report = createDraftBopReport();
        when(bopReportRepository.save(any())).thenReturn(report);
        reportingDomainService.markAsSubmitted(report);
        assertEquals("SUBMITTED", report.getReportStatus());
        verify(eventPublisher).publishEvent(any(ReportSubmittedEvent.class));
    }

    @Test
    @DisplayName("Mark as accepted transitions SUBMITTED→ACCEPTED")
    void testMarkAsAccepted() {
        BopReport report = createDraftBopReport();
        report.submit();
        when(bopReportRepository.save(any())).thenReturn(report);
        reportingDomainService.markAsAccepted(report, "REG2026001");
        assertEquals("ACCEPTED", report.getReportStatus());
    }

    @Test
    @DisplayName("Mark as rejected transitions SUBMITTED→REJECTED")
    void testMarkAsRejected() {
        BopReport report = createDraftBopReport();
        report.submit();
        when(bopReportRepository.save(any())).thenReturn(report);
        reportingDomainService.markAsRejected(report, "数据格式错误");
        assertEquals("REJECTED", report.getReportStatus());
    }
}
