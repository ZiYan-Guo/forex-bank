package com.forex.clearing.domain.repository;

import com.forex.clearing.domain.model.aggregate.SettlementBatch;

import java.util.Optional;

public interface SettlementBatchRepository {

    SettlementBatch save(SettlementBatch batch);

    Optional<SettlementBatch> findById(Long id);

    Optional<SettlementBatch> findByBatchNo(String batchNo);
}
