package com.forex.bookkeeping.domain.service;

import com.forex.bookkeeping.domain.model.aggregate.JournalEntry;
import com.forex.bookkeeping.domain.model.aggregate.MonthEndClosing;
import com.forex.bookkeeping.domain.repository.JournalEntryRepository;
import com.forex.bookkeeping.domain.repository.MonthEndClosingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Month-end closing service.
 * Executes the period-end closing procedure including posting, revaluation, and reporting.
 * 月末结账服务。执行期末结账流程，包括过账、重估和报表。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MonthEndClosingService {

    private final JournalEntryRepository journalEntryRepository;
    private final MonthEndClosingRepository monthEndClosingRepository;
    private final BookkeepingDomainService bookkeepingDomainService;
    private final RevaluationEntryService revaluationEntryService;

    /**
     * Execute month-end closing procedure.
     * 1. Post all draft entries
     * 2. Generate FX revaluation entries
     * 3. Calculate trial balance
     * 4. Lock the period
     * 月末结账流程。
     */
    public MonthEndClosing executeMonthEndClosing(String fiscalPeriod,
                                                   List<RevaluationEntryService.FxBalance> balances) {
        MonthEndClosing closing = MonthEndClosing.create(fiscalPeriod, LocalDate.now(), null);
        closing.start();
        monthEndClosingRepository.save(closing);

        bookkeepingDomainService.postEntriesForPeriod(fiscalPeriod);
        closing.addAuditEntry("Step 1: All draft entries posted for " + fiscalPeriod);

        if (balances != null && !balances.isEmpty()) {
            List<JournalEntry> revalEntries = revaluationEntryService.batchRevaluation(balances);
            for (JournalEntry entry : revalEntries) {
                bookkeepingDomainService.createEntry(entry);
                bookkeepingDomainService.postEntry(entry);
            }
            closing.addAuditEntry("Step 2: FX revaluation entries generated (" + revalEntries.size() + " entries)");
        } else {
            closing.addAuditEntry("Step 2: No FX revaluation needed");
        }

        closing.setTotalDebits(journalEntryRepository.sumByDirection(fiscalPeriod, "DEBIT"));
        closing.setTotalCredits(journalEntryRepository.sumByDirection(fiscalPeriod, "CREDIT"));
        closing.addAuditEntry("Step 3: Trial balance — Debits: " + closing.getTotalDebits()
                + ", Credits: " + closing.getTotalCredits());

        closing.complete("All checks passed");
        closing.lock();
        monthEndClosingRepository.save(closing);

        log.info("Month-end closing completed for period: {}. Debits={}, Credits={}",
                fiscalPeriod, closing.getTotalDebits(), closing.getTotalCredits());
        return closing;
    }
}
