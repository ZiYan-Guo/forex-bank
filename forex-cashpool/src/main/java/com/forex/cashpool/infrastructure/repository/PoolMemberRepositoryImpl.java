package com.forex.cashpool.infrastructure.repository;

import com.forex.cashpool.domain.model.entity.PoolMember;
import com.forex.cashpool.domain.repository.PoolMemberRepository;
import com.forex.cashpool.infrastructure.mapper.PoolMemberMapper;
import com.forex.cashpool.infrastructure.persistence.PoolMemberPO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Pool member repository implementation.
 * 资金池成员仓储实现。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PoolMemberRepositoryImpl implements PoolMemberRepository {

    private final PoolMemberMapper poolMemberMapper;

    @Override
    public PoolMember save(PoolMember member) {
        PoolMemberPO po = toPO(member);
        if (member.getId() == null) {
            poolMemberMapper.insert(po);
            log.info("Pool member created: poolId={}, accountId={}", po.getPoolId(), po.getMemberAccountId());
        } else {
            poolMemberMapper.updateById(po);
            log.info("Pool member updated: id={}, poolId={}", po.getId(), po.getPoolId());
        }
        return toDomain(po);
    }

    @Override
    public List<PoolMember> findByPoolId(String poolId) {
        return poolMemberMapper.selectByPoolId(poolId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteByPoolId(String poolId) {
        poolMemberMapper.deleteByMap(java.util.Map.of("pool_id", poolId));
        log.info("Pool members deleted for poolId={}", poolId);
    }

    private PoolMember toDomain(PoolMemberPO po) {
        return new PoolMember(
                po.getId(), po.getPoolId(), po.getMemberAccountId(),
                po.getMemberType(), po.getCurrency(), po.getSettlementMode(),
                po.getContributionLimit(), po.getJoinDate());
    }

    private PoolMemberPO toPO(PoolMember member) {
        PoolMemberPO po = new PoolMemberPO();
        po.setId(member.getId());
        po.setPoolId(member.getPoolId());
        po.setMemberAccountId(member.getMemberAccountId());
        po.setMemberType(member.getMemberType());
        po.setCurrency(member.getCurrency());
        po.setSettlementMode(member.getSettlementMode());
        po.setContributionLimit(member.getContributionLimit());
        po.setJoinDate(member.getJoinDate());
        return po;
    }
}
