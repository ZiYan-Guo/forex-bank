package com.forex.supplychain.domain.repository;

import com.forex.supplychain.domain.model.aggregate.ForfaitingContract;

import java.util.List;
import java.util.Optional;

public interface ForfaitingContractRepository {
    void save(ForfaitingContract contract);
    Optional<ForfaitingContract> findById(Long id);
    Optional<ForfaitingContract> findByContractNo(String contractNo);
    List<ForfaitingContract> findByExporter(Long exporterId);
}
