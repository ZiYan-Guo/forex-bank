package com.forex.bookkeeping.infrastructure.repository;

import com.forex.bookkeeping.domain.model.aggregate.MonthEndClosing;
import com.forex.bookkeeping.domain.repository.MonthEndClosingRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MonthEndClosingRepositoryImpl.
 * 月末结账仓储集成测试。
 */
@SpringBootTest
@ActiveProfiles("test")
class MonthEndClosingRepositoryImplTest {

    @Autowired
    private MonthEndClosingRepository monthEndClosingRepository;

    @Test
    @DisplayName("Save and find month-end closing by closingId")
    void testSaveAndFindByClosingId() {
        MonthEndClosing closing = MonthEndClosing.create("202606", LocalDate.of(2026, 6, 30), null);
        closing.start();
        closing.setTotalDebits(new BigDecimal("50000.00"));
        closing.setTotalCredits(new BigDecimal("50000.00"));
        closing.complete("All checks passed");
        closing.lock();
        MonthEndClosing saved = monthEndClosingRepository.save(closing);

        assertNotNull(saved.getId());
        assertNotNull(saved.getClosingId());

        Optional<MonthEndClosing> found = monthEndClosingRepository.findByClosingId(saved.getClosingId());
        assertTrue(found.isPresent());
        assertEquals("LOCKED", found.get().getClosingStatus());
    }

    @Test
    @DisplayName("Find by fiscal period returns matching closings")
    void testFindByFiscalPeriod() {
        MonthEndClosing closing = MonthEndClosing.create("202605", LocalDate.of(2026, 5, 31), null);
        closing.start();
        closing.complete("OK");
        closing.lock();
        monthEndClosingRepository.save(closing);

        var closings = monthEndClosingRepository.findByFiscalPeriod("202605");
        assertFalse(closings.isEmpty());
        assertEquals(1, closings.size());
    }
}
