/**
 * Foreign currency revaluation service.
 * Generates FX revaluation journal entries for month-end closing.
 * 外币重估服务。生成月末外币重估会计分录。
 */
package com.forex.bookkeeping.domain.service;

import com.forex.bookkeeping.domain.model.aggregate.JournalEntry;
import com.forex.bookkeeping.domain.repository.JournalEntryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RevaluationEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private static final String ACCT_FX_ASSET = "1002";
    private static final String ACCT_FX_LIABILITY = "2002";
    private static final String ACCT_EXCHANGE_GAIN_LOSS = "6061";

    /**
     * Generate FX revaluation entries for all foreign currency monetary items.
     * 生成外币重估分录。
     *
     * @param currency Foreign currency code
     * @param oldRate  Opening/previous exchange rate
     * @param newRate  Current/month-end exchange rate
     * @param balance  Foreign currency balance
     * @return List of journal entries (debit + credit pair)
     */
    public List<JournalEntry> generateRevaluationEntries(String currency, BigDecimal oldRate,
                                                          BigDecimal newRate, BigDecimal balance) {
        BigDecimal oldCny = balance.multiply(oldRate);
        BigDecimal newCny = balance.multiply(newRate);
        BigDecimal diff = newCny.subtract(oldCny);
        String period = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String voucherPrefix = "FXR" + period;

        List<JournalEntry> entries = new ArrayList<>();
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            String voucherNo = voucherPrefix + generateSuffix();
            JournalEntry debit = JournalEntry.create(voucherNo, LocalDate.now(), period,
                    "REVALUATION", voucherNo, currency, diff,
                    JournalEntry.DIRECTION_DEBIT, ACCT_FX_ASSET, "银行存款-外币",
                    ACCT_EXCHANGE_GAIN_LOSS, "月末外币重估-汇兑收益", null);
            JournalEntry credit = JournalEntry.create(voucherNo + "C", LocalDate.now(), period,
                    "REVALUATION", voucherNo, "CNY", diff,
                    JournalEntry.DIRECTION_CREDIT, ACCT_EXCHANGE_GAIN_LOSS, "汇兑损益",
                    ACCT_FX_ASSET, "月末外币重估-汇兑收益", null);
            entries.add(debit);
            entries.add(credit);
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            String voucherNo = voucherPrefix + generateSuffix();
            JournalEntry debit = JournalEntry.create(voucherNo, LocalDate.now(), period,
                    "REVALUATION", voucherNo, "CNY", diff.abs(),
                    JournalEntry.DIRECTION_DEBIT, ACCT_EXCHANGE_GAIN_LOSS, "汇兑损益",
                    ACCT_FX_ASSET, "月末外币重估-汇兑损失", null);
            JournalEntry credit = JournalEntry.create(voucherNo + "C", LocalDate.now(), period,
                    "REVALUATION", voucherNo, currency, diff.abs(),
                    JournalEntry.DIRECTION_CREDIT, ACCT_FX_ASSET, "银行存款-外币",
                    ACCT_EXCHANGE_GAIN_LOSS, "月末外币重估-汇兑损失", null);
            entries.add(debit);
            entries.add(credit);
        }
        return entries;
    }

    /**
     * Batch generate for multiple currencies.
     * 批量生成多币种重估分录。
     */
    public List<JournalEntry> batchRevaluation(List<FxBalance> balances) {
        List<JournalEntry> allEntries = new ArrayList<>();
        for (FxBalance b : balances) {
            allEntries.addAll(generateRevaluationEntries(b.currency, b.oldRate, b.newRate, b.balance));
        }
        return allEntries;
    }

    private String generateSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    @Data
    public static class FxBalance {
        private String currency;
        private BigDecimal oldRate;
        private BigDecimal newRate;
        private BigDecimal balance;
    }
}
