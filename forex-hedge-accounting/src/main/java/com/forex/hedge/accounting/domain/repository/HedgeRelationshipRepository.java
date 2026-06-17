package com.forex.hedge.accounting.domain.repository;

import com.forex.hedge.accounting.domain.model.aggregate.HedgeRelationship;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for HedgeRelationship aggregate.
 * 套期关系仓储接口。
 */
public interface HedgeRelationshipRepository {

    HedgeRelationship save(HedgeRelationship relationship);

    Optional<HedgeRelationship> findById(Long id);

    Optional<HedgeRelationship> findByRelationId(String relationId);

    List<HedgeRelationship> findByCustomerId(Long customerId);
}
