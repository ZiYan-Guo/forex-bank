package com.forex.risk.infrastructure.repository;

import com.forex.risk.domain.model.entity.MonitorRule;
import com.forex.risk.domain.repository.MonitorRuleRepository;
import com.forex.risk.infrastructure.mapper.MonitorRuleMapper;
import com.forex.risk.infrastructure.persistence.MonitorRulePO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MonitorRuleRepositoryImpl implements MonitorRuleRepository {

    private final MonitorRuleMapper monitorRuleMapper;

    @Override
    public MonitorRule save(MonitorRule rule) {
        MonitorRulePO po = toPO(rule);
        if (rule.getId() == null) {
            monitorRuleMapper.insert(po);
        } else {
            monitorRuleMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<MonitorRule> findById(Long id) {
        MonitorRulePO po = monitorRuleMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<MonitorRule> findByRuleCode(String ruleCode) {
        MonitorRulePO po = monitorRuleMapper.selectByRuleCode(ruleCode);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<MonitorRule> findAllEnabled() {
        return monitorRuleMapper.selectAllEnabled().stream()
                .map(this::toDomain)
                .toList();
    }

    private MonitorRule toDomain(MonitorRulePO po) {
        return new MonitorRule(
                po.getId(),
                po.getRuleCode(),
                po.getRuleName(),
                po.getRuleType(),
                po.getRiskCategory(),
                po.getRuleCondition(),
                po.getRuleAction(),
                po.getPriority(),
                po.getIsEnabled()
        );
    }

    private MonitorRulePO toPO(MonitorRule rule) {
        MonitorRulePO po = new MonitorRulePO();
        po.setId(rule.getId());
        po.setRuleCode(rule.getRuleCode());
        po.setRuleName(rule.getRuleName());
        po.setRuleType(rule.getRuleType());
        po.setRiskCategory(rule.getRiskCategory());
        po.setRuleCondition(rule.getRuleCondition());
        po.setRuleAction(rule.getRuleAction());
        po.setPriority(rule.getPriority());
        po.setIsEnabled(rule.getIsEnabled());
        return po;
    }
}
