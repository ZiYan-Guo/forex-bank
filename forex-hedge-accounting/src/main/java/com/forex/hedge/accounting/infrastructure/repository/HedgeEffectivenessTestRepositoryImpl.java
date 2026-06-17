package com.forex.hedge.accounting.infrastructure.repository;

import com.forex.hedge.accounting.domain.model.entity.HedgeEffectivenessTest;
import com.forex.hedge.accounting.domain.repository.HedgeEffectivenessTestRepository;
import com.forex.hedge.accounting.infrastructure.mapper.HedgeEffectivenessTestMapper;
import com.forex.hedge.accounting.infrastructure.persistence.HedgeEffectivenessTestPO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Hedge effectiveness test repository implementation.
 * 套期有效性测试仓储实现。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class HedgeEffectivenessTestRepositoryImpl implements HedgeEffectivenessTestRepository {

    private final HedgeEffectivenessTestMapper hedgeEffectivenessTestMapper;

    @Override
    public HedgeEffectivenessTest save(HedgeEffectivenessTest test) {
        HedgeEffectivenessTestPO po = toPO(test);
        if (test.getId() == null) {
            hedgeEffectivenessTestMapper.insert(po);
            log.info("Hedge effectiveness test created: relationId={}, testDate={}, result={}",
                    po.getRelationId(), po.getTestDate(), po.getTestResult());
        } else {
            hedgeEffectivenessTestMapper.updateById(po);
            log.info("Hedge effectiveness test updated: id={}, result={}", po.getId(), po.getTestResult());
        }
        return toDomain(po);
    }

    @Override
    public List<HedgeEffectivenessTest> findByRelationId(String relationId) {
        return hedgeEffectivenessTestMapper.selectByRelationId(relationId).stream()
                .map(this::toDomain)
                .toList();
    }

    private HedgeEffectivenessTest toDomain(HedgeEffectivenessTestPO po) {
        return new HedgeEffectivenessTest(
                po.getId(), po.getRelationId(), po.getTestDate(),
                po.getTestType(), po.getTestMethod(), po.getTestResult(),
                po.getResultStatus(), po.getRemarks());
    }

    private HedgeEffectivenessTestPO toPO(HedgeEffectivenessTest test) {
        HedgeEffectivenessTestPO po = new HedgeEffectivenessTestPO();
        po.setId(test.getId());
        po.setRelationId(test.getRelationId());
        po.setTestDate(test.getTestDate());
        po.setTestType(test.getTestType());
        po.setTestMethod(test.getTestMethod());
        po.setTestResult(test.getTestResult());
        po.setResultStatus(test.getResultStatus());
        po.setRemarks(test.getRemarks());
        return po;
    }
}
