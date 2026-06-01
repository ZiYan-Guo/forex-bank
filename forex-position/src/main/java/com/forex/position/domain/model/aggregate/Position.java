package com.forex.position.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import lombok.Getter;

import java.math.BigDecimal;
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
    /** Normal position within limits. 正常，敞口在限额内。 */
    public static final String RISK_NORMAL = "NORMAL";
    /** Warning - approaching limit. 预警，接近限额。 */
    public static final String RISK_WARNING = "WARNING";  
    /** Breach - exceeded limit. 超限，已突破限额。 */
    public static final String RISK_BREACH = "BREACH";

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
    }

    /**
     * Check position limit and update risk level.
     * NORMAL if usagePct < warningPct.
     * WARNING if usagePct >= warningPct && < 100%.
     * BREACH if usagePct >= 100%.
     * 检查敞口限额并更新风险等级。
     */
    public void checkLimit(BigDecimal warningPct) {
        calculateNetPosition();
        if (positionLimit == null || positionLimit.compareTo(BigDecimal.ZERO) <= 0) {
            this.riskLevel = RISK_NORMAL;
            return;
        }
        BigDecimal netAbs = this.netPosition.abs();
        BigDecimal usagePct = netAbs.divide(positionLimit, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        this.limitUsagePct = usagePct;
        
        BigDecimal warnThreshold = warningPct != null ? warningPct : BigDecimal.valueOf(80);
        if (usagePct.compareTo(BigDecimal.valueOf(100)) >= 0) {
            this.riskLevel = RISK_BREACH;
        } else if (usagePct.compareTo(warnThreshold) >= 0) {
            this.riskLevel = RISK_WARNING;
        } else {
            this.riskLevel = RISK_NORMAL;
        }
    }

    /**
     * Check limit using default 80% warning threshold.
     * 使用默认80%预警阈值检查限额。
     */
    public void checkLimit() {
        checkLimit(BigDecimal.valueOf(80));
    }

    /**
     * Set hedging action recommendation.
     * 设置对冲建议。
     */
    public void setHedgingAction(String action) {
        this.hedgingAction = action;
        markUpdated();
    }

    public void updateRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        markUpdated();
    }

    public void assignPositionNo(String positionNo) {
        this.positionNo = positionNo;
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
