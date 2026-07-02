package com.forex.supplychain.domain.repository;

import com.forex.supplychain.domain.model.aggregate.FactoringContract;

import java.util.List;
import java.util.Optional;

public interface FactoringContractRepository {
    void save(FactoringContract contract);
    Optional<FactoringContract> findById(Long id);
    Optional<FactoringContract> findByContractNo(String contractNo);
    List<FactoringContract> findBySeller(Long sellerId);
}
