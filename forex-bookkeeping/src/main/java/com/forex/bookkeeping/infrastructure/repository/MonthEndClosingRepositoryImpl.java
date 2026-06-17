package com.forex.bookkeeping.infrastructure.repository;

import com.forex.bookkeeping.domain.model.aggregate.MonthEndClosing;
import com.forex.bookkeeping.domain.repository.MonthEndClosingRepository;
import com.forex.bookkeeping.infrastructure.mapper.MonthEndClosingMapper;
import com.forex.bookkeeping.infrastructure.persistence.MonthEndClosingPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MonthEndClosingRepositoryImpl implements MonthEndClosingRepository {

    private final MonthEndClosingMapper monthEndClosingMapper;

    @Override
    public MonthEndClosing save(MonthEndClosing closing) {
        MonthEndClosingPO po = toPO(closing);
        if (closing.getId() == null) {
            monthEndClosingMapper.insert(po);
        } else {
            monthEndClosingMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<MonthEndClosing> findById(Long id) {
        MonthEndClosingPO po = monthEndClosingMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<MonthEndClosing> findByClosingId(String closingId) {
        MonthEndClosingPO po = monthEndClosingMapper.selectByClosingId(closingId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<MonthEndClosing> findByFiscalPeriod(String fiscalPeriod) {
        return monthEndClosingMapper.selectByFiscalPeriod(fiscalPeriod).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<MonthEndClosing> findByStatus(String status) {
        return monthEndClosingMapper.selectByStatus(status).stream()
                .map(this::toDomain)
                .toList();
    }

    private MonthEndClosing toDomain(MonthEndClosingPO po) {
        return MonthEndClosing.reconstitute(
                po.getId(), po.getClosingId(), po.getFiscalPeriod(),
                po.getClosingDate(), po.getClosingStatus(),
                po.getChecklistJson(), po.getAuditTrail(),
                po.getTotalDebits(), po.getTotalCredits(),
                po.getOperatorId());
    }

    private MonthEndClosingPO toPO(MonthEndClosing closing) {
        MonthEndClosingPO po = new MonthEndClosingPO();
        po.setId(closing.getId());
        po.setClosingId(closing.getClosingId());
        po.setFiscalPeriod(closing.getFiscalPeriod());
        po.setClosingDate(closing.getClosingDate());
        po.setClosingStatus(closing.getClosingStatus());
        po.setChecklistJson(closing.getChecklistJson());
        po.setAuditTrail(closing.getAuditTrail());
        po.setTotalDebits(closing.getTotalDebits());
        po.setTotalCredits(closing.getTotalCredits());
        po.setOperatorId(closing.getOperatorId());
        po.setVersion(closing.getVersion());
        return po;
    }
}
