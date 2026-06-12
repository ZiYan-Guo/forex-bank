package com.forex.risk.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Capital account facilitation sampling rule entity.
 * 资本项目便利化抽查规则实体。
 */
@Getter
@NoArgsConstructor
public class SamplingRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Primary key ID 主键ID */
    private Long id;

    /** Rule code, unique identifier 规则编码，唯一标识 */
    private String ruleCode;

    /** Rule display name 规则显示名称 */
    private String ruleName;

    /** Rule condition in JSON format (varchar/text for JSON conditions) 规则条件JSON */
    private String conditionJson;

    /** Sampling rate percentage (0-100) 抽查比例(0-100)% */
    private BigDecimal samplingRate;

    /** Target business module: FX_EXCHANGE/FX_PAYMENT/FX_TRADING/FX_SETTLEMENT 目标业务模块 */
    private String targetModule;

    /** Effective date of this rule 生效日期 */
    private LocalDate effectiveDate;

    /** Expiry date of this rule 失效日期 */
    private LocalDate expireDate;

    /** Priority order for rule evaluation (higher = evaluated first) 优先级 */
    private Integer priority;

    /** Status: ACTIVE/INACTIVE 状态：启用/停用 */
    private String status;

    /** Whether to auto-extract inspection samples 是否自动提取样本 */
    private Boolean isAutoExtract;

    /**
     * Create a new sampling rule. 创建新的抽查规则。
     *
     * @param id              primary key 主键
     * @param ruleCode        rule code 规则编码
     * @param ruleName        rule name 规则名称
     * @param conditionJson   JSON condition string JSON条件
     * @param samplingRate    sampling rate percentage 抽查比例
     * @param targetModule    target business module 目标模块
     * @param effectiveDate   effective date 生效日期
     * @param expireDate      expiry date 失效日期
     * @param priority        priority 优先级
     * @param status          status ACTIVE/INACTIVE 状态
     * @param isAutoExtract   auto-extract flag 自动提取标志
     */
    public SamplingRule(Long id, String ruleCode, String ruleName, String conditionJson,
                        BigDecimal samplingRate, String targetModule, LocalDate effectiveDate,
                        LocalDate expireDate, Integer priority, String status, Boolean isAutoExtract) {
        this.id = id;
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
        this.conditionJson = conditionJson;
        this.samplingRate = samplingRate;
        this.targetModule = targetModule;
        this.effectiveDate = effectiveDate;
        this.expireDate = expireDate;
        this.priority = priority;
        this.status = status;
        this.isAutoExtract = isAutoExtract;
    }
}
