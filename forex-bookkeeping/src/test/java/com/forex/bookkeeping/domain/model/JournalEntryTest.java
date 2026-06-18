package com.forex.bookkeeping.domain.model;

import com.forex.bookkeeping.domain.model.aggregate.JournalEntry;
import com.forex.common.base.exception.BusinessException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JournalEntry domain aggregate.
 * 会计分录聚合根单元测试。
 */
class JournalEntryTest {

    private JournalEntry createDraftEntry() {
        return JournalEntry.create("V2026060001", LocalDate.of(2026, 6, 1), "202606",
                "FX_EXCHANGE", "FX2026060001", "USD",
                new BigDecimal("10000.00"), "DEBIT", "1001", "Cash",
                "2001", "FX settlement", 1001L);
    }

    @Test
    @DisplayName("Create journal entry sets DRAFT status")
    void testCreate() {
        JournalEntry entry = createDraftEntry();
        assertEquals("DRAFT", entry.getEntryStatus());
        assertEquals("V2026060001", entry.getVoucherNo());
    }

    @Test
    @DisplayName("Post changes status from DRAFT to POSTED")
    void testPost() {
        JournalEntry entry = createDraftEntry();
        entry.post();
        assertEquals("POSTED", entry.getEntryStatus());
        assertNotNull(entry.getPostedTime());
    }

    @Test
    @DisplayName("Post throws when not in DRAFT status")
    void testPost_InvalidStatus() {
        JournalEntry entry = createDraftEntry();
        entry.post();
        assertThrows(BusinessException.class, entry::post);
    }

    @Test
    @DisplayName("Reverse changes status from POSTED to REVERSED")
    void testReverse() {
        JournalEntry entry = createDraftEntry();
        entry.post();
        entry.reverse("RV2026060001");
        assertEquals("REVERSED", entry.getEntryStatus());
        assertEquals("RV2026060001", entry.getReversedVoucherNo());
    }

    @Test
    @DisplayName("Reverse throws when not in POSTED status")
    void testReverse_InvalidStatus() {
        JournalEntry entry = createDraftEntry();
        assertThrows(BusinessException.class, () -> entry.reverse("RV2026060001"));
    }

    @Test
    @DisplayName("Create throws when amount is zero")
    void testCreate_ZeroAmount() {
        assertThrows(BusinessException.class, () -> JournalEntry.create(
                "V2026060002", LocalDate.now(), "202606", "FX", "FX001",
                "USD", BigDecimal.ZERO, "DEBIT", "1001", "Cash",
                "2001", "Test", 1001L));
    }

    @Test
    @DisplayName("Create throws when direction is invalid")
    void testCreate_InvalidDirection() {
        assertThrows(BusinessException.class, () -> JournalEntry.create(
                "V2026060003", LocalDate.now(), "202606", "FX", "FX001",
                "USD", new BigDecimal("1000"), "INVALID", "1001", "Cash",
                "2001", "Test", 1001L));
    }

    @Test
    @DisplayName("Reconstitute restores all fields")
    void testReconstitute() {
        JournalEntry entry = JournalEntry.reconstitute(1L, "V2026060001",
                LocalDate.of(2026, 6, 1), "202606", "FX_EXCHANGE", "FX001",
                "USD", new BigDecimal("5000.00"), "CREDIT", "2001", "Payables",
                "1001", "Payment", "POSTED", "RV001",
                LocalDate.of(2026, 6, 2).atStartOfDay(), 1001L);
        assertEquals(1L, entry.getId());
        assertEquals("POSTED", entry.getEntryStatus());
        assertEquals("RV001", entry.getReversedVoucherNo());
    }
}
