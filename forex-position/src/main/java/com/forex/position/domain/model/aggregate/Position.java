package com.forex.position.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Getter
/** Foreign exchange position aggregate root. 外汇敞口聚合根。 */
public class Position extends BaseAggregate {

    private Long id;
    private String positionNo;
    private String currencyPair;
    private String positionType;
    private String positionCurrency;
    private BigDecimal longAmount;
    private BigDecimal shortAmount;
    private BigDecimal netPosition;
    private BigDecimal positionLimit;
    private BigDecimal limitUsagePct;
    private LocalDate positionDate;
    private Long traderId;
    private String branchCode;
    private String riskLevel;
    private String hedgingAction;

    private Position() {
        super();
    }

    public static Position create(String currencyPair, String positionType,
                                   String positionCurrency, BigDecimal positionLimit,
                                   LocalDate positionDate, Long traderId, String branchCode) {
        Position position = new Position();
        position.currencyPair = currencyPair;
        position.positionType = positionType;
        position.positionCurrency = positionCurrency;
        position.positionLimit = positionLimit;
        position.positionDate = positionDate;
        position.traderId = traderId;
        position.branchCode = branchCode;
        position.longAmount = BigDecimal.ZERO;
        position.shortAmount = BigDecimal.ZERO;
        position.netPosition = BigDecimal.ZERO;
        position.limitUsagePct = BigDecimal.ZERO;
        position.riskLevel = "LOW";
        position.validate();
        return position;
    }

    public static Position reconstitute(Long id, String positionNo, String currencyPair,
                                         String positionType, String positionCurrency,
                                         BigDecimal longAmount, BigDecimal shortAmount,
                                         BigDecimal netPosition, BigDecimal positionLimit,
                                         BigDecimal limitUsagePct, LocalDate positionDate,
                                         Long traderId, String branchCode, String riskLevel,
                                         String hedgingAction) {
        Position position = new Position();
        position.id = id;
        position.positionNo = positionNo;
        position.currencyPair = currencyPair;
        position.positionType = positionType;
        position.positionCurrency = positionCurrency;
        position.longAmount = longAmount;
        position.shortAmount = shortAmount;
        position.netPosition = netPosition;
        position.positionLimit = positionLimit;
        position.limitUsagePct = limitUsagePct;
        position.positionDate = positionDate;
        position.traderId = traderId;
        position.branchCode = branchCode;
        position.riskLevel = riskLevel;
        position.hedgingAction = hedgingAction;
        return position;
    }

    /** Add long/short position amount. 增加多头/空头。 */
    public void addLong(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("多头金额不能为负数");
        }
        this.longAmount = this.longAmount.add(amount);
        calculateNetPosition();
        markUpdated();
    }

    /** Add long/short position amount. 增加多头/空头。 */
    public void addShort(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("空头金额不能为负数");
        }
        this.shortAmount = this.shortAmount.add(amount);
        calculateNetPosition();
        markUpdated();
    }

    /** Calculate net = long - short. 计算净头寸。 */
    public void calculateNetPosition() {
        this.netPosition = this.longAmount.subtract(this.shortAmount);
        updateLimitUsagePct();
    }

    /** Check if net position exceeds limit. Recalculates before checking. 检查是否超限(先重算)。 */
    public void checkLimit() {
        calculateNetPosition();
        if (positionLimit == null || positionLimit.compareTo(BigDecimal.ZERO) <= 0) {
            this.riskLevel = "LOW";
            return;
        }
        BigDecimal netAbs = this.netPosition.abs();
        if (netAbs.compareTo(positionLimit) > 0) {
            this.riskLevel = "HIGH";
        } else {
            this.riskLevel = "LOW";
        }
    }

    public void updateRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        markUpdated();
    }

    public void assignPositionNo(String positionNo) {
        this.positionNo = positionNo;
    }

    private void updateLimitUsagePct() {
        if (positionLimit == null || positionLimit.compareTo(BigDecimal.ZERO) == 0) {
            this.limitUsagePct = BigDecimal.ZERO;
            return;
        }
        this.limitUsagePct = this.netPosition.abs()
                .divide(positionLimit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    protected void validate() {
        if (currencyPair == null || currencyPair.isBlank()) {
            throw new IllegalArgumentException("货币对不能为空");
        }
        if (positionType == null || positionType.isBlank()) {
            throw new IllegalArgumentException("头寸类型不能为空");
        }
        if (positionCurrency == null || positionCurrency.isBlank()) {
            throw new IllegalArgumentException("头寸币种不能为空");
        }
    }
}
