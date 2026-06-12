package com.forex.trading.domain.model.aggregate;

import com.forex.common.base.BaseAggregate;
import com.forex.common.exception.BusinessException;
import com.forex.trading.domain.model.enums.TradeStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 增强型外汇交易聚合根
 * 完善交易状态机和业务规则
 */
@Getter
public class FxTradeEnhanced extends BaseAggregate {
    
    // 报价有效期（分钟）
    private static final int QUOTE_VALIDITY_MINUTES = 30;
    
    private String tradeNo;
    private Long customerId;
    private String tradeType;           // SPOT/FORWARD/SWAP/OPTION
    private String dealType;             // BUY/SELL
    private String buyCurrency;
    private String sellCurrency;
    private BigDecimal buyAmount;
    private BigDecimal sellAmount;
    private BigDecimal tradeRate;
    private LocalDate valueDate;
    private LocalDate maturityDate;
    private TradeStatus status;
    
    // 报价相关
    private LocalDateTime quotedAt;
    private BigDecimal quotedRate;
    
    // 确认相关
    private LocalDateTime confirmedAt;
    private Long confirmedBy;
    
    // 执行相关
    private LocalDateTime executedAt;
    private BigDecimal executedRate;
    
    // 结算相关
    private LocalDateTime settledAt;
    
    // 取消相关
    private LocalDateTime cancelledAt;
    private String cancelReason;
    
    private FxTradeEnhanced() {
        super();
    }
    
    /**
     * 创建新交易
     */
    public static FxTradeEnhanced create(Long customerId, String tradeType, String dealType,
                                         String buyCurrency, String sellCurrency,
                                         BigDecimal buyAmount, BigDecimal sellAmount,
                                         LocalDate valueDate) {
        FxTradeEnhanced trade = new FxTradeEnhanced();
        trade.customerId = customerId;
        trade.tradeType = tradeType;
        trade.dealType = dealType;
        trade.buyCurrency = buyCurrency;
        trade.sellCurrency = sellCurrency;
        trade.buyAmount = buyAmount;
        trade.sellAmount = sellAmount;
        trade.valueDate = valueDate;
        trade.status = TradeStatus.DRAFT;
        trade.validate();
        return trade;
    }
    
    /**
     * 报价
     */
    public void quote(BigDecimal rate) {
        if (this.status != TradeStatus.DRAFT) {
            throw new BusinessException("E301", "仅草稿状态的交易可以报价，当前状态: " + this.status.getDescription());
        }
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("E302", "汇率必须大于0");
        }
        
        this.quotedRate = rate;
        this.quotedAt = LocalDateTime.now();
        this.status = TradeStatus.QUOTED;
        markUpdated();
    }
    
    /**
     * 确认报价
     */
    public void confirm() {
        if (this.status != TradeStatus.QUOTED) {
            throw new BusinessException("E303", "仅已报价的交易可以确认，当前状态: " + this.status.getDescription());
        }
        
        // 检查报价是否过期
        LocalDateTime expiryTime = this.quotedAt.plusMinutes(QUOTE_VALIDITY_MINUTES);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            this.status = TradeStatus.EXPIRED;
            markUpdated();
            throw new BusinessException("E304", "报价已过期，无法确认");
        }
        
        this.confirmedAt = LocalDateTime.now();
        this.status = TradeStatus.CONFIRMED;
        markUpdated();
    }
    
    /**
     * 执行交易
     */
    public void execute(BigDecimal executedRate) {
        if (this.status != TradeStatus.CONFIRMED) {
            throw new BusinessException("E305", "仅已确认的交易可以执行，当前状态: " + this.status.getDescription());
        }
        if (executedRate == null || executedRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("E306", "执行汇率必须大于0");
        }
        
        this.executedRate = executedRate;
        this.executedAt = LocalDateTime.now();
        this.status = TradeStatus.EXECUTED;
        markUpdated();
    }
    
    /**
     * 结算交易
     */
    public void settle() {
        if (this.status != TradeStatus.EXECUTED) {
            throw new BusinessException("E307", "仅已执行的交易可以结算，当前状态: " + this.status.getDescription());
        }
        
        this.settledAt = LocalDateTime.now();
        this.status = TradeStatus.SETTLED;
        markUpdated();
    }
    
    /**
     * 取消交易
     */
    public void cancel(String reason) {
        if (this.status == TradeStatus.SETTLED || this.status == TradeStatus.CANCELLED) {
            throw new BusinessException("E308", "已结算或已取消的交易不能再次取消");
        }
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("E309", "取消原因不能为空");
        }
        
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();
        this.status = TradeStatus.CANCELLED;
        markUpdated();
    }
    
    /**
     * 业务规则验证
     */
    @Override
    protected void validate() {
        if (customerId == null) {
            throw new BusinessException("E401", "客户ID不能为空");
        }
        if (tradeType == null || tradeType.isBlank()) {
            throw new BusinessException("E402", "交易类型不能为空");
        }
        if (!isValidTradeType(tradeType)) {
            throw new BusinessException("E403", "无效的交易类型: " + tradeType);
        }
        if (dealType == null || dealType.isBlank()) {
            throw new BusinessException("E404", "交易方向不能为空");
        }
        if (!isValidDealType(dealType)) {
            throw new BusinessException("E405", "无效的交易方向: " + dealType);
        }
        if (buyCurrency == null || buyCurrency.isBlank()) {
            throw new BusinessException("E406", "买入币种不能为空");
        }
        if (sellCurrency == null || sellCurrency.isBlank()) {
            throw new BusinessException("E407", "卖出币种不能为空");
        }
        if (buyCurrency.equals(sellCurrency)) {
            throw new BusinessException("E408", "买入币种和卖出币种不能相同");
        }
        if (buyAmount == null || buyAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("E409", "买入金额必须大于0");
        }
        if (sellAmount == null || sellAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("E410", "卖出金额必须大于0");
        }
    }
    
    private boolean isValidTradeType(String tradeType) {
        return tradeType.equals("SPOT") || tradeType.equals("FORWARD") || 
               tradeType.equals("SWAP") || tradeType.equals("OPTION");
    }
    
    private boolean isValidDealType(String dealType) {
        return dealType.equals("BUY") || dealType.equals("SELL");
    }
}
