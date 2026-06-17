package com.forex.cashpool.domain.repository;

import com.forex.cashpool.domain.model.aggregate.OverseasLending;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OverseasLending aggregate.
 * 境外放款仓储接口。
 */
public interface OverseasLendingRepository {

    OverseasLending save(OverseasLending lending);

    Optional<OverseasLending> findById(Long id);

    Optional<OverseasLending> findByContractNo(String contractNo);

    List<OverseasLending> findByCustomerId(Long customerId);
}
