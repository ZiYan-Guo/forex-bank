package com.forex.hedge.accounting.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

/**
 * 套期关系聚合根.
 * Hedge Relationship aggregate root — the core domain object linking a hedged item
 * (被套期项目) with a hedging instrument (套期工具) under IFRS 9 / ASC 815.
 */
@Getter
public class HedgeRelationship extends BaseAggregate {

    /** 主键ID / Primary key. */
    private Long id;

    /** 套期关系编号 / Business relation identifier. */
    private String relationId;

    /** 客户ID / Customer identifier. */
    private Long customerId;

    /** 套期类型: FAIR_VALUE(公允价值套期) / CASH_FLOW(现金流量套期) / NET_INVESTMENT(净投资套期). */
    private String hedgeType;

    /** 被套期项目描述 / Description of the hedged item (exposure). */
    private String hedgedItem;

    /** 套期工具描述, e.g. "FX Forward USD/CNY". */
    private String hedgingInstrument;

    /** 被套期金额 / Hedged amount in hedged currency. */
    private BigDecimal hedgedAmount;

    /** 被套期币种 / Currency of the hedged item. */
    private String hedgedCurrency;

    /** 套期工具名义本金 / Notional principal of the hedging instrument. */
    private BigDecimal instrumentNotional;

    /** 指定日 / Date the hedge relationship was designated. */
    private LocalDate designationDate;

    /** 取消指定日 / Date the hedge relationship was de-designated. */
    private LocalDate deDesignationDate;

    /** 套期关系状态: DESIGNATED / EFFECTIVE / INEFFECTIVE / DEDESIGNATED. */
    private String relationshipStatus;

    /** 有效性比率 (0.8–1.25为有效区间) / Effectiveness ratio. */
    private BigDecimal effectivenessRatio;

    /** 适用会计准则: IFRS9 / ASC815. */
    private String ifrsStandard;

    /** Constructor for creating a new hedge relationship. 构建新的套期关系. */
    public static HedgeRelationship create(Long id, String relationId, Long customerId,
                                           String hedgeType, String hedgedItem,
                                           String hedgingInstrument, BigDecimal hedgedAmount,
                                           String hedgedCurrency, BigDecimal instrumentNotional,
                                           String ifrsStandard) {
        HedgeRelationship hr = new HedgeRelationship();
        hr.id = id;
        hr.relationId = relationId;
        hr.customerId = customerId;
        hr.hedgeType = hedgeType;
        hr.hedgedItem = hedgedItem;
        hr.hedgingInstrument = hedgingInstrument;
        hr.hedgedAmount = hedgedAmount;
        hr.hedgedCurrency = hedgedCurrency;
        hr.instrumentNotional = instrumentNotional;
        hr.ifrsStandard = ifrsStandard;
        hr.relationshipStatus = "DESIGNATED";
        hr.validate();
        return hr;
    }

    /** Reconstitute from persistence. 从持久层重建聚合. */
    public static HedgeRelationship reconstitute(Long id, String relationId, Long customerId,
                                                  String hedgeType, String hedgedItem,
                                                  String hedgingInstrument, BigDecimal hedgedAmount,
                                                  String hedgedCurrency, BigDecimal instrumentNotional,
                                                  LocalDate designationDate, LocalDate deDesignationDate,
                                                  String relationshipStatus, BigDecimal effectivenessRatio,
                                                  String ifrsStandard) {
        HedgeRelationship hr = new HedgeRelationship();
        hr.id = id;
        hr.relationId = relationId;
        hr.customerId = customerId;
        hr.hedgeType = hedgeType;
        hr.hedgedItem = hedgedItem;
        hr.hedgingInstrument = hedgingInstrument;
        hr.hedgedAmount = hedgedAmount;
        hr.hedgedCurrency = hedgedCurrency;
        hr.instrumentNotional = instrumentNotional;
        hr.designationDate = designationDate;
        hr.deDesignationDate = deDesignationDate;
        hr.relationshipStatus = relationshipStatus;
        hr.effectivenessRatio = effectivenessRatio;
        hr.ifrsStandard = ifrsStandard;
        return hr;
    }

    /** 正式指定套期关系 / Designate the hedge relationship. */
    public void designate() {
        this.designationDate = LocalDate.now();
        this.relationshipStatus = "DESIGNATED";
        markUpdated();
    }

    /** 标记为有效套期 / Mark the hedge as effective with the given ratio. */
    public void markEffective(BigDecimal ratio) {
        this.effectivenessRatio = ratio;
        this.relationshipStatus = "EFFECTIVE";
        markUpdated();
    }

    /** 标记为无效套期 / Mark the hedge as ineffective with a reason. */
    public void markIneffective(String reason) {
        this.relationshipStatus = "INEFFECTIVE";
        markUpdated();
    }

    /** 取消套期指定 / De-designate the hedge relationship. */
    public void deDesignate() {
        this.deDesignationDate = LocalDate.now();
        this.relationshipStatus = "DEDESIGNATED";
        markUpdated();
    }

    /** 记录套期无效部分 / Record the ineffective portion of the hedge. */
    public void recordIneffectiveness(BigDecimal amount) {
        this.relationshipStatus = "INEFFECTIVE";
        markUpdated();
    }

    /** 领域校验 / Validate invariants. */
    @Override
    protected void validate() {
        if (relationId == null || relationId.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "套期关系编号不能为空 / relationId must not be blank");
        }
        if (customerId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "客户ID不能为空 / customerId must not be null");
        }
        if (hedgeType == null || hedgeType.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "套期类型不能为空 / hedgeType must not be blank");
        }
        if (hedgedAmount != null && hedgedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "被套期金额必须大于0 / hedgedAmount must be positive");
        }
    }
}
