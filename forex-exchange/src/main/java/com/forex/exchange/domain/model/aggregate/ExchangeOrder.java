package com.forex.exchange.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import com.forex.exchange.domain.model.valueobject.Money;
import com.forex.exchange.domain.model.valueobject.OrderNo;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Exchange order aggregate root. Manages the lifecycle of forex exchange orders including spot,
 * forward, and pending orders.
 * 结售汇订单聚合根，管理即期、远期和挂单等结售汇订单的完整生命周期。
 */
@Getter
public class ExchangeOrder extends BaseAggregate {

    private Long id;
    /** Unique order number generated via OrderNo.generate(). 订单唯一编号，通过OrderNo.generate()生成。 */
    private String orderNo;
    private Long customerId;
    /** Order type: SPOT, FORWARD, PENDING_ORDER. 订单类型：即期/远期/挂单。 */
    private String orderType;
    /** Deal direction: BUY or SELL. 交易方向：买入/卖出。 */
    private String dealType;
    /** Base currency code. 基础货币代码。 */
    private String baseCurrency;
    /** Quote currency code. 报价货币代码。 */
    private String quoteCurrency;
    /** Order amount in base currency. 订单金额（基础货币）。 */
    private BigDecimal orderAmount;
    /** Settlement amount calculated as orderAmount * confirmedRate. 结算金额。 */
    private BigDecimal settleAmount;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    /** Confirmed exchange rate for settlement. 已确认的成交汇率。 */
    private BigDecimal confirmedRate;
    private String rateType;
    private LocalDateTime lockRateTime;
    /** Locked rate expiration time. 锁价到期时间。 */
    private LocalDateTime lockRateExpireTime;
    /** Value date for settlement. 起息日。 */
    private LocalDate valueDate;
    private LocalDate maturityDate;
    /** Current order status. 订单当前状态。 */
    private String orderStatus;
    private String customerAccountNo;
    private String bankAccountNo;
    private BigDecimal feeAmount;
    private BigDecimal commissionAmount;
    private String settlementType;
    private String channel;
    private Long operatorId;
    private String remark;

    private ExchangeOrder() {
        super();
    }

    /**
     * Create a new exchange order with a generated order number.
     * 创建结售汇订单，自动生成订单编号。
     */
    public static ExchangeOrder create(Long customerId, String orderType, String dealType,
                                        String baseCurrency, String quoteCurrency,
                                        BigDecimal orderAmount, String channel) {
        ExchangeOrder order = new ExchangeOrder();
        order.orderNo = OrderNo.generate("EX").getValue();
        order.customerId = customerId;
        order.orderType = orderType;
        order.dealType = dealType;
        order.baseCurrency = baseCurrency;
        order.quoteCurrency = quoteCurrency;
        order.orderAmount = orderAmount;
        order.channel = channel;
        order.orderStatus = "PENDING";
        order.validate();
        return order;
    }

    /**
     * Rebuild aggregate from database state.
     * 从数据库重建聚合。
     */
    public static ExchangeOrder reconstitute(Long id, String orderNo, Long customerId,
                                              String orderType, String dealType,
                                              String baseCurrency, String quoteCurrency,
                                              BigDecimal orderAmount, BigDecimal settleAmount,
                                              BigDecimal bidRate, BigDecimal askRate,
                                              BigDecimal confirmedRate, String rateType,
                                              LocalDateTime lockRateTime, LocalDateTime lockRateExpireTime,
                                              LocalDate valueDate, LocalDate maturityDate,
                                              String orderStatus, String customerAccountNo,
                                              String bankAccountNo, BigDecimal feeAmount,
                                              BigDecimal commissionAmount, String settlementType,
                                              String channel, Long operatorId, String remark) {
        ExchangeOrder order = new ExchangeOrder();
        order.id = id;
        order.orderNo = orderNo;
        order.customerId = customerId;
        order.orderType = orderType;
        order.dealType = dealType;
        order.baseCurrency = baseCurrency;
        order.quoteCurrency = quoteCurrency;
        order.orderAmount = orderAmount;
        order.settleAmount = settleAmount;
        order.bidRate = bidRate;
        order.askRate = askRate;
        order.confirmedRate = confirmedRate;
        order.rateType = rateType;
        order.lockRateTime = lockRateTime;
        order.lockRateExpireTime = lockRateExpireTime;
        order.valueDate = valueDate;
        order.maturityDate = maturityDate;
        order.orderStatus = orderStatus;
        order.customerAccountNo = customerAccountNo;
        order.bankAccountNo = bankAccountNo;
        order.feeAmount = feeAmount;
        order.commissionAmount = commissionAmount;
        order.settlementType = settlementType;
        order.channel = channel;
        order.operatorId = operatorId;
        order.remark = remark;
        return order;
    }

    /**
     * Confirm order with final rate and calculate settlement amount.
     * 确认成交汇率，计算结算金额。
     */
    public void confirm(BigDecimal rate) {
        this.confirmedRate = rate;
        this.orderStatus = "CONFIRMED";
        this.settleAmount = calculateSettleAmount();
        markUpdated();
    }

    /**
     * Lock the exchange rate for the specified duration in seconds.
     * 锁定汇率指定秒数。
     */
    public void lockRate(BigDecimal rate, int lockSeconds) {
        this.bidRate = rate;
        this.lockRateTime = LocalDateTime.now();
        this.lockRateExpireTime = LocalDateTime.now().plusSeconds(lockSeconds);
        this.orderStatus = "RATE_LOCKED";
        markUpdated();
    }

    /**
     * Assign bid/ask rates and rate type to the order. 设置买卖汇率及汇率类型。
     */
    public void assignRate(BigDecimal bidRate, BigDecimal askRate, String rateType) {
        this.bidRate = bidRate;
        this.askRate = askRate;
        this.rateType = rateType;
    }

    /**
     * Assign value date and maturity date to the order. 设定起息日和到期日。
     */
    public void assignDates(LocalDate valueDate, LocalDate maturityDate) {
        this.valueDate = valueDate;
        this.maturityDate = maturityDate;
    }

    /**
     * Assign customer account and fee details. 设置客户账户及费用信息。
     */
    public void assignAccountInfo(String customerAccountNo, BigDecimal feeAmount,
                                   BigDecimal commissionAmount, String settlementType) {
        this.customerAccountNo = customerAccountNo;
        this.feeAmount = feeAmount != null ? feeAmount : BigDecimal.ZERO;
        this.commissionAmount = commissionAmount != null ? commissionAmount : BigDecimal.ZERO;
        this.settlementType = settlementType;
    }

    /**
     * Assign a remark to the order. 设置订单备注。
     */
    public void assignRemark(String remark) {
        this.remark = remark;
    }

    /**
     * Cancel a pending order with the given reason.
     * 取消待处理订单。
     */
    public void cancel(String reason) {
        if (!"PENDING".equals(this.orderStatus)) {
            throw new BusinessException("只能取消待处理状态的订单");
        }
        this.orderStatus = "CANCELLED";
        this.remark = reason;
        markUpdated();
    }

    /**
     * Reverse a confirmed or successful order. 冲正已确认或已成功的订单。
     */
    public void markReversed() {
        if (!"SUCCESS".equals(this.orderStatus) && !"CONFIRMED".equals(this.orderStatus)) {
            throw new BusinessException("只能冲正成功或已确认状态的订单");
        }
        this.orderStatus = "REVERSED";
        markUpdated();
    }

    public BigDecimal calculateSettleAmount() {
        if (orderAmount == null || confirmedRate == null) {
            return BigDecimal.ZERO;
        }
        return orderAmount.multiply(confirmedRate);
    }

    /**
     * Check if the locked rate has expired. 检查锁价是否已过期。
     */
    public boolean isRateExpired() {
        if (lockRateExpireTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(lockRateExpireTime);
    }

    @Override
    protected void validate() {
        if (customerId == null) {
            throw new BusinessException("客户ID不能为空");
        }
        if (orderType == null || orderType.isBlank()) {
            throw new BusinessException("订单类型不能为空");
        }
        if (dealType == null || dealType.isBlank()) {
            throw new BusinessException("交易方向不能为空");
        }
        if (baseCurrency == null || baseCurrency.isBlank()) {
            throw new BusinessException("基础货币不能为空");
        }
        if (quoteCurrency == null || quoteCurrency.isBlank()) {
            throw new BusinessException("报价货币不能为空");
        }
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("订单金额必须大于0");
        }
    }
}
