package com.forex.clearing.infrastructure.repository;

import com.forex.clearing.domain.model.aggregate.SettlementBatch;
import com.forex.clearing.domain.repository.SettlementBatchRepository;
import com.forex.clearing.infrastructure.mapper.SettlementBatchMapper;
import com.forex.clearing.infrastructure.persistence.SettlementBatchPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SettlementBatchRepositoryImpl implements SettlementBatchRepository {

    private final SettlementBatchMapper mapper;

    @Override
    public SettlementBatch save(SettlementBatch batch) {
        SettlementBatchPO po = toPO(batch);
        if (batch.getId() == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<SettlementBatch> findById(Long id) {
        SettlementBatchPO po = mapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<SettlementBatch> findByBatchNo(String batchNo) {
        SettlementBatchPO po = mapper.selectByBatchNo(batchNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    private SettlementBatch toDomain(SettlementBatchPO po) {
        return SettlementBatch.reconstitute(
                po.getId(),
                po.getBatchNo(),
                po.getBatchDate(),
                po.getClearingChannel(),
                po.getTotalCount(),
                po.getTotalAmount(),
                po.getNetAmount(),
                po.getBatchStatus()
        );
    }

    private SettlementBatchPO toPO(SettlementBatch batch) {
        SettlementBatchPO po = new SettlementBatchPO();
        po.setId(batch.getId());
        po.setBatchNo(batch.getBatchNo());
        po.setBatchDate(batch.getBatchDate());
        po.setClearingChannel(batch.getClearingChannel());
        po.setTotalCount(batch.getTotalCount());
        po.setTotalAmount(batch.getTotalAmount());
        po.setNetAmount(batch.getNetAmount());
        po.setBatchStatus(batch.getBatchStatus());
        po.setVersion(batch.getVersion());
        return po;
    }
}
