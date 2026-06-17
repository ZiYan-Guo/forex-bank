package com.forex.risk.infrastructure.repository;

import com.forex.risk.domain.model.entity.SamplingRule;
import com.forex.risk.domain.repository.SamplingRuleRepository;
import com.forex.risk.infrastructure.mapper.SamplingRuleMapper;
import com.forex.risk.infrastructure.persistence.SamplingRulePO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Sampling rule repository implementation.
 * 抽查规则仓储实现。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SamplingRuleRepositoryImpl implements SamplingRuleRepository {

    private final SamplingRuleMapper samplingRuleMapper;

    @Override
    public SamplingRule save(SamplingRule rule) {
        SamplingRulePO po = toPO(rule);
        if (rule.getId() == null) {
            samplingRuleMapper.insert(po);
            log.info("Sampling rule created: ruleCode={}, ruleName={}, rate={}%", po.getRuleCode(), po.getRuleName(), po.getSamplingRate());
        } else {
            samplingRuleMapper.updateById(po);
            log.info("Sampling rule updated: id={}, ruleCode={}", po.getId(), po.getRuleCode());
        }
        return toDomain(po);
    }

    @Override
    public Optional<SamplingRule> findById(Long id) {
        SamplingRulePO po = samplingRuleMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<SamplingRule> findByRuleCode(String ruleCode) {
        SamplingRulePO po = samplingRuleMapper.selectByRuleCode(ruleCode);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<SamplingRule> findAllActive() {
        return samplingRuleMapper.selectAllActive().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        samplingRuleMapper.deleteById(id);
    }

    private SamplingRule toDomain(SamplingRulePO po) {
        return new SamplingRule(
                po.getId(),
                po.getRuleCode(),
                po.getRuleName(),
                po.getConditionJson(),
                po.getSamplingRate(),
                po.getTargetModule(),
                po.getEffectiveDate(),
                po.getExpireDate(),
                po.getPriority(),
                po.getStatus(),
                po.getIsAutoExtract()
        );
    }

    private SamplingRulePO toPO(SamplingRule rule) {
        SamplingRulePO po = new SamplingRulePO();
        po.setId(rule.getId());
        po.setRuleCode(rule.getRuleCode());
        po.setRuleName(rule.getRuleName());
        po.setConditionJson(rule.getConditionJson());
        po.setSamplingRate(rule.getSamplingRate());
        po.setTargetModule(rule.getTargetModule());
        po.setEffectiveDate(rule.getEffectiveDate());
        po.setExpireDate(rule.getExpireDate());
        po.setPriority(rule.getPriority());
        po.setStatus(rule.getStatus());
        po.setIsAutoExtract(rule.getIsAutoExtract());
        return po;
    }
}
