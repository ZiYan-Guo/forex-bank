package com.forex.bookkeeping.application.service;

import com.forex.bookkeeping.application.command.CreateEntryCmd;
import com.forex.bookkeeping.domain.model.aggregate.JournalEntry;
import com.forex.bookkeeping.domain.model.query.JournalQuery;
import com.forex.bookkeeping.domain.repository.JournalEntryRepository;
import com.forex.bookkeeping.domain.service.BookkeepingDomainService;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.exception.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookkeepingAppService {

    private final BookkeepingDomainService bookkeepingDomainService;
    private final JournalEntryRepository journalEntryRepository;

    public JournalEntry createJournalEntry(CreateEntryCmd cmd) {
        String voucherNo = bookkeepingDomainService.generateVoucherNo("JV");
        String fiscalPeriod = String.format("%d%02d", cmd.getVoucherDate().getYear(),
                cmd.getVoucherDate().getMonthValue());

        JournalEntry entry = JournalEntry.create(
                voucherNo, cmd.getVoucherDate(), fiscalPeriod,
                cmd.getBizType(), cmd.getBizNo(), cmd.getCurrency(),
                cmd.getAmount(), cmd.getEntryDirection(), cmd.getAccountCode(),
                null, cmd.getOppositeAccountCode(),
                cmd.getSummary(), null);

        return bookkeepingDomainService.createEntry(entry);
    }

    @RedisLock(key = "#voucherNo")
    public void postEntry(String voucherNo) {
        JournalEntry entry = journalEntryRepository.findByVoucherNo(voucherNo)
                .orElseThrow(() -> new BusinessException("凭证不存在"));
        bookkeepingDomainService.postEntry(entry);
    }

    @RedisLock(key = "#voucherNo")
    public JournalEntry reverseEntry(String voucherNo, String reason) {
        JournalEntry original = journalEntryRepository.findByVoucherNo(voucherNo)
                .orElseThrow(() -> new BusinessException("凭证不存在"));

        String reverseVoucherNo = bookkeepingDomainService.generateVoucherNo("RV");
        String fiscalPeriod = String.format("%d%02d",
                LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        JournalEntry reversal = JournalEntry.create(
                reverseVoucherNo, LocalDate.now(), fiscalPeriod,
                original.getBizType(), original.getBizNo(), original.getCurrency(),
                original.getAmount(),
                JournalEntry.DIRECTION_DEBIT.equals(original.getEntryDirection())
                        ? JournalEntry.DIRECTION_CREDIT : JournalEntry.DIRECTION_DEBIT,
                original.getAccountCode(), null, original.getOppositeAccountCode(),
                reason != null ? reason : "冲正:" + original.getVoucherNo(), null);

        return bookkeepingDomainService.reverseEntry(original, reversal);
    }

    public JournalEntry getEntryDetail(String voucherNo) {
        return journalEntryRepository.findByVoucherNo(voucherNo)
                .orElseThrow(() -> new BusinessException("凭证不存在"));
    }

    public PageResp<JournalEntry> pageQuery(JournalQuery query) {
        return journalEntryRepository.pageQuery(query);
    }

    public void dailyClosing(LocalDate date) {
        JournalQuery query = new JournalQuery();
        query.setVoucherDate(date);
        query.setEntryStatus(JournalEntry.STATUS_DRAFT);
        query.setPageNum(1);
        query.setPageSize(200);

        PageResp<JournalEntry> page = journalEntryRepository.pageQuery(query);
        List<JournalEntry> pendingEntries = page.getRecords();

        for (JournalEntry entry : pendingEntries) {
            bookkeepingDomainService.postEntry(entry);
        }
    }
}
