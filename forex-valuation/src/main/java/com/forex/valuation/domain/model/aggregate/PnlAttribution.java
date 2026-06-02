package com.forex.valuation.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class PnlAttribution extends BaseAggregate {

    private Long id;
    private String attribNo;
    private Long tradeId;
    private String tradeNo;
    private LocalDate attribDate;
    private BigDecimal totalPnl;
    private BigDecimal deltaPnl;
    private BigDecimal thetaPnl;
    private BigDecimal gammaPnl;
    private BigDecimal vegaPnl;
    private BigDecimal carryPnl;
    private BigDecimal tradePnl;
    private String tariffType;
    private String tariffValue;

    private PnlAttribution() {
        super();
    }

    public static PnlAttribution create(Long tradeId, String tradeNo, LocalDate attribDate,
                                         String tariffType, String tariffValue) {
        PnlAttribution attr = new PnlAttribution();
        attr.tradeId = tradeId;
        attr.tradeNo = tradeNo;
        attr.attribDate = attribDate;
        attr.tariffType = tariffType;
        attr.tariffValue = tariffValue;
        attr.totalPnl = BigDecimal.ZERO;
        attr.deltaPnl = BigDecimal.ZERO;
        attr.thetaPnl = BigDecimal.ZERO;
        attr.gammaPnl = BigDecimal.ZERO;
        attr.vegaPnl = BigDecimal.ZERO;
        attr.carryPnl = BigDecimal.ZERO;
        attr.tradePnl = BigDecimal.ZERO;
        attr.validate();
        return attr;
    }

    public static PnlAttribution reconstitute(Long id, String attribNo, Long tradeId,
                                               String tradeNo, LocalDate attribDate,
                                               BigDecimal totalPnl, BigDecimal deltaPnl,
                                               BigDecimal thetaPnl, BigDecimal gammaPnl,
                                               BigDecimal vegaPnl, BigDecimal carryPnl,
                                               BigDecimal tradePnl, String tariffType,
                                               String tariffValue) {
        PnlAttribution attr = new PnlAttribution();
        attr.id = id;
        attr.attribNo = attribNo;
        attr.tradeId = tradeId;
        attr.tradeNo = tradeNo;
        attr.attribDate = attribDate;
        attr.totalPnl = totalPnl;
        attr.deltaPnl = deltaPnl;
        attr.thetaPnl = thetaPnl;
        attr.gammaPnl = gammaPnl;
        attr.vegaPnl = vegaPnl;
        attr.carryPnl = carryPnl;
        attr.tradePnl = tradePnl;
        attr.tariffType = tariffType;
        attr.tariffValue = tariffValue;
        return attr;
    }

    public void populatePnlComponents(BigDecimal deltaPnl, BigDecimal thetaPnl,
                                       BigDecimal gammaPnl, BigDecimal vegaPnl,
                                       BigDecimal carryPnl, BigDecimal tradePnl) {
        this.deltaPnl = deltaPnl != null ? deltaPnl : BigDecimal.ZERO;
        this.thetaPnl = thetaPnl != null ? thetaPnl : BigDecimal.ZERO;
        this.gammaPnl = gammaPnl != null ? gammaPnl : BigDecimal.ZERO;
        this.vegaPnl = vegaPnl != null ? vegaPnl : BigDecimal.ZERO;
        this.carryPnl = carryPnl != null ? carryPnl : BigDecimal.ZERO;
        this.tradePnl = tradePnl != null ? tradePnl : BigDecimal.ZERO;
        calculateTotal();
    }

    public void calculateTotal() {
        this.totalPnl = BigDecimal.ZERO
                .add(this.deltaPnl != null ? this.deltaPnl : BigDecimal.ZERO)
                .add(this.thetaPnl != null ? this.thetaPnl : BigDecimal.ZERO)
                .add(this.gammaPnl != null ? this.gammaPnl : BigDecimal.ZERO)
                .add(this.vegaPnl != null ? this.vegaPnl : BigDecimal.ZERO)
                .add(this.carryPnl != null ? this.carryPnl : BigDecimal.ZERO)
                .add(this.tradePnl != null ? this.tradePnl : BigDecimal.ZERO);
        markUpdated();
    }

    @Override
    protected void validate() {
        if (tradeId == null) {
            throw new IllegalArgumentException("交易ID不能为空");
        }
        if (attribDate == null) {
            throw new IllegalArgumentException("归因日期不能为空");
        }
    }
}
