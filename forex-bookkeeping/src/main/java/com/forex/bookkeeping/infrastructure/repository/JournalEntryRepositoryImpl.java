package com.forex.bookkeeping.infrastructure.repository;

import com.forex.bookkeeping.domain.model.aggregate.JournalEntry;
import com.forex.bookkeeping.domain.model.query.JournalQuery;
import com.forex.bookkeeping.domain.repository.JournalEntryRepository;
import com.forex.bookkeeping.infrastructure.mapper.JournalEntryMapper;
import com.forex.bookkeeping.infrastructure.persistence.JournalEntryPO;
import com.forex.common.base.dto.PageResp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JournalEntryRepositoryImpl implements JournalEntryRepository {

    private final JournalEntryMapper journalEntryMapper;

    @Override
    public JournalEntry save(JournalEntry entry) {
        JournalEntryPO po = toPO(entry);
        if (entry.getId() == null) {
            journalEntryMapper.insert(po);
        } else {
            journalEntryMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<JournalEntry> findById(Long id) {
        JournalEntryPO po = journalEntryMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<JournalEntry> findByVoucherNo(String voucherNo) {
        JournalEntryPO po = journalEntryMapper.selectByVoucherNo(voucherNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<JournalEntry> findByBizNo(String bizNo) {
        JournalEntryPO po = journalEntryMapper.selectByBizNo(bizNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<JournalEntry> pageQuery(JournalQuery query) {
        Page<JournalEntryPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        List<JournalEntryPO> poList = journalEntryMapper.pageQuery(page, query);
        page.setRecords(poList);

        List<JournalEntry> records = page.getRecords().stream().map(this::toDomain).toList();
        return PageResp.of(page.getTotal(), records, (int) page.getCurrent(), (int) page.getSize());
    }

    private JournalEntry toDomain(JournalEntryPO po) {
        return JournalEntry.reconstitute(
                po.getId(),
                po.getVoucherNo(),
                po.getVoucherDate(),
                po.getFiscalPeriod(),
                po.getBizType(),
                po.getBizNo(),
                po.getCurrency(),
                po.getAmount(),
                po.getEntryDirection(),
                po.getAccountCode(),
                po.getAccountName(),
                po.getOppositeAccountCode(),
                po.getSummary(),
                po.getEntryStatus(),
                po.getReversedVoucherNo(),
                po.getPostedTime(),
                po.getOperatorId()
        );
    }

    private JournalEntryPO toPO(JournalEntry entry) {
        JournalEntryPO po = new JournalEntryPO();
        po.setId(entry.getId());
        po.setVoucherNo(entry.getVoucherNo());
        po.setVoucherDate(entry.getVoucherDate());
        po.setFiscalPeriod(entry.getFiscalPeriod());
        po.setBizType(entry.getBizType());
        po.setBizNo(entry.getBizNo());
        po.setCurrency(entry.getCurrency());
        po.setAmount(entry.getAmount());
        po.setEntryDirection(entry.getEntryDirection());
        po.setAccountCode(entry.getAccountCode());
        po.setAccountName(entry.getAccountName());
        po.setOppositeAccountCode(entry.getOppositeAccountCode());
        po.setSummary(entry.getSummary());
        po.setEntryStatus(entry.getEntryStatus());
        po.setReversedVoucherNo(entry.getReversedVoucherNo());
        po.setPostedTime(entry.getPostedTime());
        po.setOperatorId(entry.getOperatorId());
        po.setVersion(entry.getVersion());
        return po;
    }
}
