package com.forex.hedge.accounting.domain.repository;

import com.forex.hedge.accounting.domain.model.entity.HedgeEffectivenessTest;

import java.util.List;

/**
 * Repository interface for HedgeEffectivenessTest entity.
 * 套期有效性测试仓储接口。
 */
public interface HedgeEffectivenessTestRepository {

    HedgeEffectivenessTest save(HedgeEffectivenessTest test);

    List<HedgeEffectivenessTest> findByRelationId(String relationId);
}
