package com.forex.cashpool.infrastructure.repository;

import com.forex.cashpool.domain.model.aggregate.CashPool;
import com.forex.cashpool.domain.model.entity.PoolMember;
import com.forex.cashpool.domain.repository.CashPoolRepository;
import com.forex.cashpool.infrastructure.mapper.CashPoolMapper;
import com.forex.cashpool.infrastructure.persistence.CashPoolPO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Cash pool repository implementation.
 * 资金池仓储实现。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CashPoolRepositoryImpl implements CashPoolRepository {

    private final CashPoolMapper cashPoolMapper;

    @Override
    public CashPool save(CashPool pool) {
        CashPoolPO po = toPO(pool);
        if (pool.getId() == null) {
            cashPoolMapper.insert(po);
            log.info("Cash pool created: poolId={}, poolName={}, currency={}", po.getPoolId(), po.getPoolName(), po.getPoolCurrency());
        } else {
            cashPoolMapper.updateById(po);
            log.info("Cash pool updated: id={}, poolId={}, status={}", po.getId(), po.getPoolId(), po.getPoolStatus());
        }
        return toDomain(po);
    }

    @Override
    public Optional<CashPool> findById(Long id) {
        CashPoolPO po = cashPoolMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<CashPool> findByPoolId(String poolId) {
        CashPoolPO po = cashPoolMapper.selectByPoolId(poolId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    private CashPool toDomain(CashPoolPO po) {
        return CashPool.reconstitute(
                po.getId(), po.getPoolId(), po.getMainAccountId(),
                po.getPoolName(), po.getPoolCurrency(),
                po.getTotalLimit(), po.getUsedLimit(), po.getAvailableLimit(),
                po.getPoolStatus(), po.getEffectiveDate(), null);
    }

    private CashPoolPO toPO(CashPool pool) {
        CashPoolPO po = new CashPoolPO();
        po.setId(pool.getId());
        po.setPoolId(pool.getPoolId());
        po.setMainAccountId(pool.getMainAccountId());
        po.setPoolName(pool.getPoolName());
        po.setPoolCurrency(pool.getPoolCurrency());
        po.setTotalLimit(pool.getTotalLimit());
        po.setUsedLimit(pool.getUsedLimit());
        po.setAvailableLimit(pool.getAvailableLimit());
        po.setPoolStatus(pool.getPoolStatus());
        po.setEffectiveDate(pool.getEffectiveDate());
        po.setVersion(pool.getVersion());
        return po;
    }
}
