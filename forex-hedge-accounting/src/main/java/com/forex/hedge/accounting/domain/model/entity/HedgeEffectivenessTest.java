package com.forex.hedge.accounting.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 套期有效性测试实体.
 * Hedge effectiveness test entity — captures the result of a prospective or retrospective
 * effectiveness assessment (IAS 39 / IFRS 9).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HedgeEffectivenessTest extends BaseEntity {

    /** 主键ID / Primary key. */
    private Long id;

    /** 套期关系编号 / Reference to hedge relationship. */
    private String relationId;

    /** 测试日期 / Date the test was performed. */
    private LocalDate testDate;

    /** 测试类型: PROSPECTIVE(预期有效性) / RETROSPECTIVE(追溯有效性). */
    private String testType;

    /** 测试方法: DOLLAR_OFFSET(美元抵补法) / REGRESSION(回归分析法) / VARIABILITY_REDUCTION(变动减少法). */
    private String testMethod;

    /** 测试结果比率 (0.8–1.25为可接受区间) / Test result ratio. */
    private BigDecimal testResult;

    /** 结果状态: PASS(通过) / FAIL(失败). */
    private String resultStatus;

    /** 备注 / Remarks. */
    private String remarks;

    /** 判断测试是否通过 / Returns true if the effectiveness test passed. */
    public boolean isPassed() {
        return "PASS".equals(resultStatus);
    }
}
