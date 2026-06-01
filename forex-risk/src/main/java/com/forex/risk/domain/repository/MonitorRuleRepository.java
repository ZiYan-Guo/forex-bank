package com.forex.risk.domain.repository;

import com.forex.risk.domain.model.entity.MonitorRule;

import java.util.List;
import java.util.Optional;

public interface MonitorRuleRepository {

    MonitorRule save(MonitorRule rule);

    Optional<MonitorRule> findById(Long id);

    Optional<MonitorRule> findByRuleCode(String ruleCode);

    List<MonitorRule> findAllEnabled();
}
