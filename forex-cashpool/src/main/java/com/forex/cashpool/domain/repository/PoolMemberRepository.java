package com.forex.cashpool.domain.repository;

import com.forex.cashpool.domain.model.entity.PoolMember;

import java.util.List;

/**
 * Repository interface for PoolMember entity.
 * 资金池成员仓储接口。
 */
public interface PoolMemberRepository {

    PoolMember save(PoolMember member);

    List<PoolMember> findByPoolId(String poolId);

    void deleteByPoolId(String poolId);
}
