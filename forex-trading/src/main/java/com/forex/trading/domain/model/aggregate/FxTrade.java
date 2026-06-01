package com.forex.trading.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * FX trade aggregate root. Manages forex trading lifecycle: spot, forward, swap, and option trades.
 * 外汇交易聚合根，管理即期、远期、掉期和期权等外汇交易的全生命周期。
 */
@Getter
public class FxTrade extends BaseAggregate {

    private static final Set<String> VALID_TRADE_TYPES = Set.of("SPOT", "FORWARD", "SWAP", "OPTION");
    private static final Set<String> VALID_DEAL_TYPES = Set.of("BUY", "SELL");
    private static final Set<String> VALID_OPTION_TYPES = Set.of("CALL", "PUT");

    private Long id;
    /** Unique trade number. 交易唯一编号。 */
    private String tradeNo;
    private Long customerId;
    /** Trade type: SPOT, FORWARD, SWAP, OPTION. 交易类型：即期/远期/掉期/期权。 */
    private String tradeType;
    /** Deal direction: BUY or SELL. 交易方向：买入/卖出。 */
    private String dealType;
    /** Bought currency code. 买入货币代码。 */
    private String buyCurrency;
    /** Sold currency code. 卖出货币代码。 */
    private String sellCurrency;
    private BigDecimal buyAmount;
    private BigDecimal sellAmount;
    private BigDecimal tradeRate;
    /** Value date for settlement. 起息日。 */
    private LocalDate valueDate;
    /** Maturity date for forward/swap trades. 到期日（远期/掉期交易）。 */
    private LocalDate maturityDate;
    private LocalDate nearValueDate;
    private LocalDate farValueDate;
    private BigDecimal nearRate;
    private BigDecimal farRate;
    /** Swap points for swap trades. 掉期点（掉期交易专用）。 */
    private BigDecimal swapPoints;
    /** Option type: CALL or PUT. 期权类型：看涨/看跌。 */
    private String optionType;
    /** Strike price for option trades. 行权价（期权交易专用）。 */
    private BigDecimal strikePrice;
    private BigDecimal premiumAmount;
    private String premiumCurrency;
    private LocalDate premiumDate;
    /** Expiry date for option trades. 到期日（期权交易专用）。 */
    private LocalDate expiryDate;
    private String deliveryType;
    /** Current trade status. 交易当前状态。 */
    private String tradeStatus;
    private String settlementStatus;
    private String nostroAccount;
    private String counterparty;
    private String tradeChannel;
    private Long operatorId;
    private LocalDateTime confirmTime;
    private LocalDateTime executeTime;
    private LocalDateTime settleTime;
    private String remark;

    private FxTrade() {
        super();
    }

    /**
     * Create a new FX trade. 创建外汇交易。
     */
    public static FxTrade create(String tradeNo, Long customerId, String tradeType, String dealType,
                                  String buyCurrency, String sellCurrency, BigDecimal buyAmount,
                                  BigDecimal sellAmount, BigDecimal tradeRate, LocalDate valueDate,
                                  String tradeChannel, Long operatorId) {
        FxTrade trade = new FxTrade();
        trade.tradeNo = tradeNo;
        trade.customerId = customerId;
        trade.tradeType = tradeType;
        trade.dealType = dealType;
        trade.buyCurrency = buyCurrency;
        trade.sellCurrency = sellCurrency;
        trade.buyAmount = buyAmount;
        trade.sellAmount = sellAmount;
        trade.tradeRate = tradeRate;
        trade.valueDate = valueDate;
        trade.tradeChannel = tradeChannel;
        trade.operatorId = operatorId;
        trade.tradeStatus = "PENDING";
        trade.validate();
        return trade;
    }

    /**
     * Rebuild aggregate from database. 从数据库重建聚合。
     */
    public static FxTrade reconstitute(Long id, String tradeNo, Long customerId, String tradeType,
                                        String dealType, String buyCurrency, String sellCurrency,
                                        BigDecimal buyAmount, BigDecimal sellAmount, BigDecimal tradeRate,
                                        LocalDate valueDate, LocalDate maturityDate,
                                        LocalDate nearValueDate, LocalDate farValueDate,
                                        BigDecimal nearRate, BigDecimal farRate, BigDecimal swapPoints,
                                        String optionType, BigDecimal strikePrice, BigDecimal premiumAmount,
                                        String premiumCurrency, LocalDate premiumDate, LocalDate expiryDate,
                                        String deliveryType, String tradeStatus, String settlementStatus,
                                        String nostroAccount, String counterparty, String tradeChannel,
                                        Long operatorId, LocalDateTime confirmTime, LocalDateTime executeTime,
                                        LocalDateTime settleTime, String remark) {
        FxTrade trade = new FxTrade();
        trade.id = id;
        trade.tradeNo = tradeNo;
        trade.customerId = customerId;
        trade.tradeType = tradeType;
        trade.dealType = dealType;
        trade.buyCurrency = buyCurrency;
        trade.sellCurrency = sellCurrency;
        trade.buyAmount = buyAmount;
        trade.sellAmount = sellAmount;
        trade.tradeRate = tradeRate;
        trade.valueDate = valueDate;
        trade.maturityDate = maturityDate;
        trade.nearValueDate = nearValueDate;
        trade.farValueDate = farValueDate;
        trade.nearRate = nearRate;
        trade.farRate = farRate;
        trade.swapPoints = swapPoints;
        trade.optionType = optionType;
        trade.strikePrice = strikePrice;
        trade.premiumAmount = premiumAmount;
        trade.premiumCurrency = premiumCurrency;
        trade.premiumDate = premiumDate;
        trade.expiryDate = expiryDate;
        trade.deliveryType = deliveryType;
        trade.tradeStatus = tradeStatus;
        trade.settlementStatus = settlementStatus;
        trade.nostroAccount = nostroAccount;
        trade.counterparty = counterparty;
        trade.tradeChannel = tradeChannel;
        trade.operatorId = operatorId;
        trade.confirmTime = confirmTime;
        trade.executeTime = executeTime;
        trade.settleTime = settleTime;
        trade.remark = remark;
        return trade;
    }

    /**
     * Confirm the pending trade. 确认交易。
     */
    public void confirm() {
        if (!"PENDING".equals(this.tradeStatus)) {
            throw new BusinessException("只能确认待处理状态的交易");
        }
        this.tradeStatus = "CONFIRMED";
        this.confirmTime = LocalDateTime.now();
        markUpdated();
    }

    /**
     * Execute the confirmed trade. 执行交易。
     */
    public void execute() {
        if (!"CONFIRMED".equals(this.tradeStatus)) {
            throw new BusinessException("只能执行已确认状态的交易");
        }
        this.tradeStatus = "EXECUTED";
        this.executeTime = LocalDateTime.now();
        markUpdated();
    }

    /**
     * Settle the executed trade. 结算交易。
     */
    public void settle() {
        if (!"EXECUTED".equals(this.tradeStatus)) {
            throw new BusinessException("只能结算已执行状态的交易");
        }
        this.tradeStatus = "SETTLED";
        this.settleTime = LocalDateTime.now();
        markUpdated();
    }

    /**
     * Roll over the settled trade to a new value date with a new rate. 展期交易。
     */
    public void rollOver(LocalDate newDate, BigDecimal newRate) {
        if (!"SETTLED".equals(this.tradeStatus)) {
            throw new BusinessException("只能展期已结算状态的交易");
        }
        this.tradeStatus = "ROLLED_OVER";
        this.valueDate = newDate;
        this.tradeRate = newRate;
        markUpdated();
    }

    /**
     * Close out the executed trade. 平仓交易。
     */
    public void closeOut() {
        if (!"EXECUTED".equals(this.tradeStatus)) {
            throw new BusinessException("只能平仓已执行状态的交易");
        }
        this.tradeStatus = "CLOSED_OUT";
        markUpdated();
    }

    /**
     * Cancel a pending or confirmed trade. 取消交易。
     */
    public void cancel(String reason) {
        if (!"PENDING".equals(this.tradeStatus) && !"CONFIRMED".equals(this.tradeStatus)) {
            throw new BusinessException("只能取消待处理或已确认状态的交易");
        }
        this.tradeStatus = "CANCELLED";
        this.remark = reason;
        markUpdated();
    }

    /**
     * Expire an unexercised option trade. 期权过期作废。
     */
    public void expire() {
        if (!"OPTION".equals(this.tradeType)) {
            throw new BusinessException("只有期权交易才能过期作废");
        }
        if (!"PENDING".equals(this.tradeStatus) && !"CONFIRMED".equals(this.tradeStatus)) {
            throw new BusinessException("只能将待处理或已确认状态的期权交易过期作废");
        }
        this.tradeStatus = "EXPIRED";
        markUpdated();
    }

    @Override
    protected void validate() {
        if (tradeNo == null || tradeNo.isBlank()) {
            throw new BusinessException("交易编号不能为空");
        }
        if (tradeType == null || !VALID_TRADE_TYPES.contains(tradeType)) {
            throw new BusinessException("无效的交易类型: " + tradeType);
        }
        if (dealType != null && !VALID_DEAL_TYPES.contains(dealType)) {
            throw new BusinessException("无效的交易方向: " + dealType);
        }
        if (optionType != null && !VALID_OPTION_TYPES.contains(optionType)) {
            throw new BusinessException("无效的期权类型: " + optionType);
        }
        if (buyCurrency != null && buyCurrency.equals(sellCurrency)) {
            throw new BusinessException("买入货币和卖出货币不能相同");
        }
    }
}
