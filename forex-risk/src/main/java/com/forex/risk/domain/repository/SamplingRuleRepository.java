package com.forex.risk.domain.repository;

import com.forex.risk.domain.model.entity.SamplingRule;

import java.util.List;
import java.util.Optional;

/**
 * Sampling rule repository interface.
 * 抽查规则仓储接口。
 */
public interface SamplingRuleRepository {

    /** Save a sampling rule. 保存抽查规则。 */
    SamplingRule save(SamplingRule rule);

    /** Find by primary key. 根据主键查询。 */
    Optional<SamplingRule> findById(Long id);

    /** Find by rule code. 根据规则编码查询。 */
    Optional<SamplingRule> findByRuleCode(String ruleCode);

    /** Find all active and enabled rules. 查询所有启用状态的有效规则。 */
    List<SamplingRule> findAllActive();

    /** Delete by id. 根据ID删除。 */
    void deleteById(Long id);
}
