package com.forex.cashpool.domain.repository;

import com.forex.cashpool.domain.model.aggregate.CashPool;

import java.util.Optional;

/**
 * Repository interface for CashPool aggregate.
 * 资金池仓储接口。
 */
public interface CashPoolRepository {

    CashPool save(CashPool pool);

    Optional<CashPool> findById(Long id);

    Optional<CashPool> findByPoolId(String poolId);
}
