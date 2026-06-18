package com.forex.hedge.accounting.infrastructure.repository;

import com.forex.hedge.accounting.domain.model.aggregate.HedgeRelationship;
import com.forex.hedge.accounting.domain.repository.HedgeRelationshipRepository;
import com.forex.hedge.accounting.infrastructure.mapper.HedgeRelationshipMapper;
import com.forex.hedge.accounting.infrastructure.persistence.HedgeRelationshipPO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Hedge relationship repository implementation.
 * 套期关系仓储实现。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class HedgeRelationshipRepositoryImpl implements HedgeRelationshipRepository {

    private final HedgeRelationshipMapper hedgeRelationshipMapper;

    @Override
    public HedgeRelationship save(HedgeRelationship relationship) {
        HedgeRelationshipPO po = toPO(relationship);
        if (relationship.getId() == null) {
            hedgeRelationshipMapper.insert(po);
            log.info("Hedge relationship created: relationId={}, type={}, customerId={}",
                    po.getRelationId(), po.getHedgeType(), po.getCustomerId());
        } else {
            hedgeRelationshipMapper.updateById(po);
            log.info("Hedge relationship updated: id={}, relationId={}, status={}",
                    po.getId(), po.getRelationId(), po.getRelationshipStatus());
        }
        return toDomain(po);
    }

    @Override
    public Optional<HedgeRelationship> findById(Long id) {
        HedgeRelationshipPO po = hedgeRelationshipMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<HedgeRelationship> findByRelationId(String relationId) {
        HedgeRelationshipPO po = hedgeRelationshipMapper.selectByRelationId(relationId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<HedgeRelationship> findByCustomerId(Long customerId) {
        return hedgeRelationshipMapper.selectByCustomerId(customerId).stream()
                .map(this::toDomain)
                .toList();
    }

    private HedgeRelationship toDomain(HedgeRelationshipPO po) {
        return HedgeRelationship.reconstitute(
                po.getId(), po.getRelationId(), po.getCustomerId(),
                po.getHedgeType(), po.getHedgedItem(), po.getHedgingInstrument(),
                po.getHedgedAmount(), po.getHedgedCurrency(), po.getInstrumentNotional(),
                po.getDesignationDate(), po.getDeDesignationDate(),
                po.getRelationshipStatus(), po.getEffectivenessRatio(),
                po.getIfrsStandard());
    }

    private HedgeRelationshipPO toPO(HedgeRelationship hr) {
        HedgeRelationshipPO po = new HedgeRelationshipPO();
        po.setId(hr.getId());
        po.setRelationId(hr.getRelationId());
        po.setCustomerId(hr.getCustomerId());
        po.setHedgeType(hr.getHedgeType());
        po.setHedgedItem(hr.getHedgedItem());
        po.setHedgingInstrument(hr.getHedgingInstrument());
        po.setHedgedAmount(hr.getHedgedAmount());
        po.setHedgedCurrency(hr.getHedgedCurrency());
        po.setInstrumentNotional(hr.getInstrumentNotional());
        po.setDesignationDate(hr.getDesignationDate());
        po.setDeDesignationDate(hr.getDeDesignationDate());
        po.setRelationshipStatus(hr.getRelationshipStatus());
        po.setEffectivenessRatio(hr.getEffectivenessRatio());
        po.setIfrsStandard(hr.getIfrsStandard());
        return po;
    }
}
