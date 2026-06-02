package com.forex.bookkeeping.domain.service;

import com.forex.bookkeeping.domain.event.EntryPostedEvent;
import com.forex.bookkeeping.domain.model.aggregate.JournalEntry;
import com.forex.bookkeeping.domain.model.valueobject.VoucherNo;
import com.forex.bookkeeping.domain.repository.JournalEntryRepository;
import com.forex.common.base.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookkeepingDomainService {

    private final JournalEntryRepository journalEntryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public JournalEntry createEntry(JournalEntry entry) {
        JournalEntry saved = journalEntryRepository.save(entry);
        log.info("Created journal entry: voucherNo={}, amount={} {}, direction={}",
                saved.getVoucherNo(), saved.getAmount(), saved.getCurrency(), saved.getEntryDirection());
        return saved;
    }

    public void postEntry(JournalEntry entry) {
        entry.post();
        journalEntryRepository.save(entry);

        eventPublisher.publishEvent(new EntryPostedEvent(
                entry.getId(), entry.getVoucherNo(), entry.getAmount()));

        log.info("Posted journal entry: voucherNo={}, amount={} {}",
                entry.getVoucherNo(), entry.getAmount(), entry.getCurrency());
    }

    public JournalEntry reverseEntry(JournalEntry original, JournalEntry reversal) {
        if (!JournalEntry.STATUS_POSTED.equals(original.getEntryStatus())) {
            throw new BusinessException("只有已过账的分录才能冲正");
        }

        String reverseVoucherNo = generateVoucherNo("RV");
        reversal.reverse(reverseVoucherNo);

        JournalEntry savedReversal = journalEntryRepository.save(reversal);
        original.reverse(reverseVoucherNo);
        journalEntryRepository.save(original);

        log.info("Reversed journal entry: originalVoucherNo={}, reverseVoucherNo={}",
                original.getVoucherNo(), reverseVoucherNo);
        return savedReversal;
    }

    public String generateVoucherNo(String prefix) {
        return VoucherNo.generate(prefix).getValue();
    }

    public void postEntriesForPeriod(String fiscalPeriod) {
        List<JournalEntry> draftEntries = journalEntryRepository.findByStatusAndPeriod(
                JournalEntry.STATUS_DRAFT, fiscalPeriod);
        for (JournalEntry entry : draftEntries) {
            postEntry(entry);
        }
        log.info("Batch posted {} draft entries for period {}", draftEntries.size(), fiscalPeriod);
    }
}
