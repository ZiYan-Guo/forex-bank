package com.forex.bookkeeping.domain.service;

import com.forex.bookkeeping.domain.event.EntryPostedEvent;
import com.forex.bookkeeping.domain.model.aggregate.JournalEntry;
import com.forex.bookkeeping.domain.repository.JournalEntryRepository;
import com.forex.common.base.exception.BusinessException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookkeepingDomainService.
 * 记账领域服务单元测试。
 */
@ExtendWith(MockitoExtension.class)
class BookkeepingDomainServiceTest {

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BookkeepingDomainService bookkeepingDomainService;

    @Captor
    private ArgumentCaptor<EntryPostedEvent> eventCaptor;

    private JournalEntry createEntry() {
        return JournalEntry.create("V2026060001", LocalDate.of(2026, 6, 1), "202606",
                "FX_EXCHANGE", "FX202606001", "USD",
                new BigDecimal("10000.00"), "DEBIT", "1001", "Cash",
                "2001", "FX settlement", 1001L);
    }

    @Test
    @DisplayName("Create entry saves and returns the entry")
    void testCreateEntry() {
        JournalEntry entry = createEntry();
        when(journalEntryRepository.save(any())).thenReturn(entry);

        JournalEntry result = bookkeepingDomainService.createEntry(entry);

        assertNotNull(result);
        verify(journalEntryRepository).save(entry);
    }

    @Test
    @DisplayName("Post entry changes status and publishes event")
    void testPostEntry() {
        JournalEntry entry = createEntry();
        when(journalEntryRepository.save(any())).thenReturn(entry);

        bookkeepingDomainService.postEntry(entry);

        assertEquals("POSTED", entry.getEntryStatus());
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals("V2026060001", eventCaptor.getValue().getVoucherNo());
        verify(journalEntryRepository, times(1)).save(entry);
    }

    @Test
    @DisplayName("Reverse entry throws when original is not posted")
    void testReverseEntry_NotPosted() {
        JournalEntry draft = createEntry();
        JournalEntry reversal = createEntry();

        assertThrows(BusinessException.class,
                () -> bookkeepingDomainService.reverseEntry(draft, reversal));
        verify(journalEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Reverse posted entry saves both original and reversal")
    void testReverseEntry_Success() {
        JournalEntry original = JournalEntry.create("V2026060001", LocalDate.of(2026, 6, 1), "202606",
                "FX_EXCHANGE", "FX202606001", "USD",
                new BigDecimal("10000.00"), "DEBIT", "1001", "Cash",
                "2001", "FX settlement", 1001L);
        original.post();
        // Reversal entry must be posted before it can be linked
        JournalEntry reversal = JournalEntry.create("V2026060002", LocalDate.of(2026, 6, 1), "202606",
                "FX_EXCHANGE", "FX202606001", "USD",
                new BigDecimal("10000.00"), "CREDIT", "2001", "Payables",
                "1001", "Reversal", 1001L);
        reversal.post();
        when(journalEntryRepository.save(any())).thenReturn(reversal);

        JournalEntry result = bookkeepingDomainService.reverseEntry(original, reversal);

        assertNotNull(result);
        verify(journalEntryRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Post entries for period fetches and posts all drafts")
    void testPostEntriesForPeriod() {
        JournalEntry e1 = createEntry();
        JournalEntry e2 = createEntry();
        when(journalEntryRepository.findByStatusAndPeriod("DRAFT", "202606"))
                .thenReturn(List.of(e1, e2));
        when(journalEntryRepository.save(any())).thenReturn(e1);

        bookkeepingDomainService.postEntriesForPeriod("202606");

        assertEquals("POSTED", e1.getEntryStatus());
        assertEquals("POSTED", e2.getEntryStatus());
        verify(eventPublisher, times(2)).publishEvent(any(EntryPostedEvent.class));
    }

    @Test
    @DisplayName("Generate voucher number returns valid format")
    void testGenerateVoucherNo() {
        String voucherNo = bookkeepingDomainService.generateVoucherNo("JV");
        assertNotNull(voucherNo);
        assertTrue(voucherNo.startsWith("JV"));
    }
}
