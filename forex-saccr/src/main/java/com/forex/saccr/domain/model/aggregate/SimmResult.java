package com.forex.saccr.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class SimmResult extends BaseAggregate {

    private Long id;
    private String calcNo;
    private Long tradeId;
    private String tradeNo;
    private LocalDate calcDate;
    private BigDecimal notionalAmount;
    private BigDecimal deltaMargin;
    private BigDecimal vegaMargin;
    private BigDecimal curvatureMargin;
    private BigDecimal totalMargin;
    private String calcMethod = "ISDA-SIMM";
    private String sensitivitiesJson;

    private SimmResult() {
        super();
    }

    public static SimmResult create(String calcNo, Long tradeId, String tradeNo,
                                     LocalDate calcDate, BigDecimal notionalAmount) {
        SimmResult result = new SimmResult();
        result.calcNo = calcNo;
        result.tradeId = tradeId;
        result.tradeNo = tradeNo;
        result.calcDate = calcDate;
        result.notionalAmount = notionalAmount;
        return result;
    }

    public static SimmResult reconstitute(Long id, String calcNo, Long tradeId,
                                           String tradeNo, LocalDate calcDate,
                                           BigDecimal notionalAmount, BigDecimal deltaMargin,
                                           BigDecimal vegaMargin, BigDecimal curvatureMargin,
                                           BigDecimal totalMargin, String calcMethod,
                                           String sensitivitiesJson) {
        SimmResult result = new SimmResult();
        result.id = id;
        result.calcNo = calcNo;
        result.tradeId = tradeId;
        result.tradeNo = tradeNo;
        result.calcDate = calcDate;
        result.notionalAmount = notionalAmount;
        result.deltaMargin = deltaMargin;
        result.vegaMargin = vegaMargin;
        result.curvatureMargin = curvatureMargin;
        result.totalMargin = totalMargin;
        result.calcMethod = calcMethod;
        result.sensitivitiesJson = sensitivitiesJson;
        return result;
    }

    public void updateResult(BigDecimal delta, BigDecimal vega, BigDecimal curvature) {
        this.deltaMargin = delta;
        this.vegaMargin = vega;
        this.curvatureMargin = curvature;
        this.totalMargin = delta.add(vega).add(curvature);
        markUpdated();
    }

    @Override
    protected void validate() {
    }
}
