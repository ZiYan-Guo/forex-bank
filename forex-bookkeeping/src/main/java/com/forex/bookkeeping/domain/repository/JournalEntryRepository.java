package com.forex.bookkeeping.domain.repository;

import com.forex.bookkeeping.domain.model.aggregate.JournalEntry;
import com.forex.bookkeeping.domain.model.query.JournalQuery;
import com.forex.common.base.dto.PageResp;

import java.util.Optional;

public interface JournalEntryRepository {

    JournalEntry save(JournalEntry entry);

    Optional<JournalEntry> findById(Long id);

    Optional<JournalEntry> findByVoucherNo(String voucherNo);

    Optional<JournalEntry> findByBizNo(String bizNo);

    PageResp<JournalEntry> pageQuery(JournalQuery query);
}
