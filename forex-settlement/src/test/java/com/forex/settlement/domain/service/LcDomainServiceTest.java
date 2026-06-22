package com.forex.settlement.domain.service;

import com.forex.settlement.domain.event.LcDocCheckedEvent;
import com.forex.settlement.domain.event.LcIssuedEvent;
import com.forex.settlement.domain.model.aggregate.LetterOfCredit;
import com.forex.settlement.domain.repository.LcRepository;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LcDomainServiceTest {

    @Mock private LcRepository lcRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Captor private ArgumentCaptor<LcIssuedEvent> issuedCaptor;
    @Captor private ArgumentCaptor<LcDocCheckedEvent> checkedCaptor;

    private LcDomainService lcDomainService;

    @BeforeEach
    void setUp() {
        lcDomainService = new LcDomainService(lcRepository, eventPublisher);
    }

    private LetterOfCredit createDraftLc() {
        return LetterOfCredit.create(1001L, "IMPORT", "INWARD",
                new BigDecimal("500000.00"), "USD",
                "APPLICANT CO", "BENEFICIARY LTD",
                "ISSUING BANK", LocalDate.now().plusMonths(3),
                "SHANGHAI", "ISSUING_BANK", "BY_PAYMENT",
                1001L, "Test LC");
    }

    @Test
    @DisplayName("Create LC assigns LC number and saves")
    void testCreateLc() {
        LetterOfCredit lc = createDraftLc();
        when(lcRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LetterOfCredit result = lcDomainService.createLc(lc);

        assertNotNull(result.getLcNo());
        assertTrue(result.getLcNo().startsWith("LCIM"));
        verify(lcRepository).save(any());
    }

    @Test
    @DisplayName("Issue LC transitions DRAFT→ISSUED and publishes event")
    void testIssueLc() {
        LetterOfCredit lc = createDraftLc();
        when(lcRepository.save(any())).thenReturn(lc);

        lcDomainService.issueLc(lc);

        assertEquals("ISSUED", lc.getLcStatus());
        verify(eventPublisher).publishEvent(issuedCaptor.capture());
        assertEquals(lc.getLcAmount(), issuedCaptor.getValue().getAmount());
    }

    @Test
    @DisplayName("Advise LC transitions ISSUED→ADVISED")
    void testAdviseLc() {
        LetterOfCredit lc = createDraftLc();
        lc.issue();
        when(lcRepository.save(any())).thenReturn(lc);

        lcDomainService.adviseLc(lc);

        assertEquals("ADVISED", lc.getLcStatus());
        verify(lcRepository, atLeastOnce()).save(lc);
    }

    @Test
    @DisplayName("Present documents transitions ADVISED→DOC_PRESENTED")
    void testPresentDocuments() {
        LetterOfCredit lc = createDraftLc();
        lc.issue();
        lc.advise();
        when(lcRepository.save(any())).thenReturn(lc);

        lcDomainService.presentDocuments(lc);

        assertEquals("DOC_PRESENTED", lc.getLcStatus());
    }

    @Test
    @DisplayName("Check documents with no discrepancy publishes event")
    void testCheckDocuments_Clean() {
        LetterOfCredit lc = createDraftLc();
        lc.issue();
        lc.advise();
        lc.presentDocuments();
        when(lcRepository.save(any())).thenReturn(lc);

        lcDomainService.checkDocuments(lc, false);

        assertEquals("DOC_CHECKED", lc.getLcStatus());
        verify(eventPublisher).publishEvent(checkedCaptor.capture());
        assertFalse(checkedCaptor.getValue().isDiscrepancy());
    }

    @Test
    @DisplayName("Check documents with discrepancy still transitions and publishes event")
    void testCheckDocuments_Discrepant() {
        LetterOfCredit lc = createDraftLc();
        lc.issue();
        lc.advise();
        lc.presentDocuments();
        when(lcRepository.save(any())).thenReturn(lc);

        lcDomainService.checkDocuments(lc, true);

        assertEquals("DOC_CHECKED", lc.getLcStatus());
        verify(eventPublisher).publishEvent(checkedCaptor.capture());
        assertTrue(checkedCaptor.getValue().isDiscrepancy());
    }

    @Test
    @DisplayName("Accept LC transitions DOC_CHECKED→ACCEPTED")
    void testAcceptLc() {
        LetterOfCredit lc = createDraftLc();
        lc.issue();
        lc.advise();
        lc.presentDocuments();
        lc.checkDocuments(false);
        when(lcRepository.save(any())).thenReturn(lc);

        lcDomainService.acceptLc(lc);

        assertEquals("ACCEPTED", lc.getLcStatus());
    }

    @Test
    @DisplayName("Pay LC transitions ACCEPTED→PAID")
    void testPayLc() {
        LetterOfCredit lc = createDraftLc();
        lc.issue();
        lc.advise();
        lc.presentDocuments();
        lc.checkDocuments(false);
        lc.accept();
        when(lcRepository.save(any())).thenReturn(lc);

        lcDomainService.payLc(lc);

        assertEquals("PAID", lc.getLcStatus());
    }

    @Test
    @DisplayName("Issue throws for non-DRAFT LC")
    void testIssueLc_InvalidState() {
        LetterOfCredit lc = createDraftLc();
        lc.issue();

        assertThrows(RuntimeException.class, () -> lcDomainService.issueLc(lc));
    }
}
