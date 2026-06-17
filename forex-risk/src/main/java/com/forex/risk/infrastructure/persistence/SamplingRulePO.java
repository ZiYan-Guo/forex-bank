package com.forex.risk.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Sampling rule persistent object.
 * 抽查规则持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sampling_rule")
public class SamplingRulePO extends BasePO {

    private String ruleCode;
    private String ruleName;
    private String conditionJson;
    private BigDecimal samplingRate;
    private String targetModule;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
    private Integer priority;
    private String status;
    private Boolean isAutoExtract;
}
