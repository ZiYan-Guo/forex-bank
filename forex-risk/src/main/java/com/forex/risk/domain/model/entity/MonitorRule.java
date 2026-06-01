package com.forex.risk.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

@Getter
public class MonitorRule extends BaseEntity {

    private Long id;
    private String ruleCode;
    private String ruleName;
    private String ruleType;
    private String riskCategory;
    private String ruleCondition;
    private String ruleAction;
    private Integer priority;
    private Integer isEnabled;

    public MonitorRule(Long id, String ruleCode, String ruleName, String ruleType,
                        String riskCategory, String ruleCondition, String ruleAction,
                        Integer priority, Integer isEnabled) {
        this.id = id;
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
        this.ruleType = ruleType;
        this.riskCategory = riskCategory;
        this.ruleCondition = ruleCondition;
        this.ruleAction = ruleAction;
        this.priority = priority;
        this.isEnabled = isEnabled;
    }
}
