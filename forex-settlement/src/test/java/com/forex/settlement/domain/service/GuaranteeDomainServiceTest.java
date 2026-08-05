package com.forex.settlement.domain.service;

import com.forex.settlement.domain.model.entity.BankGuarantee;
import com.forex.settlement.domain.repository.GuaranteeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuaranteeDomainServiceTest {

    @Mock private GuaranteeRepository guaranteeRepository;

    private GuaranteeDomainService guaranteeDomainService;

    @BeforeEach
    void setUp() {
        guaranteeDomainService = new GuaranteeDomainService(guaranteeRepository);
    }

    @Test
    @DisplayName("Create guarantee generates guarantee number and saves")
    void testCreateGuarantee() {
        when(guaranteeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BankGuarantee result = guaranteeDomainService.createGuarantee(1001L, "BID",
                new BigDecimal("200000.00"), "USD", "BENEFICIARY INC",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 1),
                LocalDate.of(2027, 6, 1), LocalDate.of(2027, 9, 1),
                "CG202606001", "URDG758", new BigDecimal("0.001500"), 1001L, "Bid bond");

        assertNotNull(result);
        assertTrue(result.getGuaranteeNo().startsWith("BG"));
        assertEquals(BankGuarantee.STATUS_DRAFT, result.getGuaranteeStatus());
        assertEquals(new BigDecimal("200000.00"), result.getGuaranteeAmount());
        verify(guaranteeRepository).save(any());
    }

    @Test
    @DisplayName("Issue guarantee saves the guarantee")
    void testIssueGuarantee() {
        BankGuarantee g = createDraftGuarantee();
        when(guaranteeRepository.save(any())).thenReturn(g);

        guaranteeDomainService.issueGuarantee(g);

        verify(guaranteeRepository).save(g);
    }

    @Test
    @DisplayName("Claim saves the guarantee")
    void testClaim() {
        BankGuarantee g = createIssuedGuarantee();
        when(guaranteeRepository.save(any())).thenReturn(g);

        guaranteeDomainService.claim(g);

        verify(guaranteeRepository).save(g);
    }

    @Test
    @DisplayName("Expire saves the guarantee")
    void testExpire() {
        BankGuarantee g = createIssuedGuarantee();
        when(guaranteeRepository.save(any())).thenReturn(g);

        guaranteeDomainService.expire(g);

        verify(guaranteeRepository).save(g);
    }

    @Test
    @DisplayName("Create guarantee with different guarantee types")
    void testCreateGuarantee_Types() {
        when(guaranteeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BankGuarantee perf = guaranteeDomainService.createGuarantee(1001L, "PERFORMANCE",
                new BigDecimal("500000.00"), "EUR", "OWNER LTD",
                LocalDate.now(), LocalDate.now(),
                LocalDate.now().plusYears(2), LocalDate.now().plusYears(2).plusMonths(3),
                "CG001", "URDG758", BigDecimal.ZERO, 1001L, "Performance bond");

        assertTrue(perf.getGuaranteeNo().startsWith("BG"));
        assertEquals("EUR", perf.getGuaranteeCurrency());
    }

    @Test
    @DisplayName("isExpired returns true when expiry passed")
    void testIsExpired_True() {
        BankGuarantee g = new BankGuarantee(null, "BG001", 1001L, "BID",
                new BigDecimal("100000.00"), "USD", "BENEF",
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31), LocalDate.of(2026, 3, 31),
                null, "URDG758", "ISSUED", BigDecimal.ZERO, BigDecimal.ZERO,
                1001L, null, null);

        assertTrue(g.isExpired());
    }

    @Test
    @DisplayName("isExpired returns false when expiry in future")
    void testIsExpired_False() {
        BankGuarantee g = new BankGuarantee(null, "BG002", 1001L, "BID",
                new BigDecimal("100000.00"), "USD", "BENEF",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 1),
                LocalDate.of(2030, 12, 31), LocalDate.of(2031, 3, 31),
                null, "URDG758", "ISSUED", BigDecimal.ZERO, BigDecimal.ZERO,
                1001L, null, null);

        assertFalse(g.isExpired());
    }

    private BankGuarantee createDraftGuarantee() {
        return new BankGuarantee(null, "BG20260601001", 1001L,
                "BID", new BigDecimal("100000.00"), "USD",
                "BENEFICIARY CO", LocalDate.now(), LocalDate.now(),
                LocalDate.now().plusYears(1), LocalDate.now().plusYears(1).plusMonths(3),
                "CG001", "URDG758", BankGuarantee.STATUS_DRAFT,
                BigDecimal.ZERO, BigDecimal.ZERO, 1001L, null, null);
    }

    private BankGuarantee createIssuedGuarantee() {
        BankGuarantee guarantee = createDraftGuarantee();
        guarantee.issue(LocalDate.now());
        return guarantee;
    }
}
