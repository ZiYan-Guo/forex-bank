package com.forex.preciousmetal.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class PreciousMetalOrder extends BaseAggregate {

    private Long id;
    private String orderNo;
    private Long customerId;
    private String metalType;
    private String tradeType;
    private String direction;
    private BigDecimal weight;
    private String weightUnit;
    private BigDecimal purity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String currency;
    private String settleCurrency;
    private String deliveryType;
    private LocalDate valueDate;
    private String deliveryLocation;
    private String storageLocation;
    private String orderStatus;
    private String riskLevel;
    private LocalDateTime createTime;

    private PreciousMetalOrder() {
        super();
    }

    public static PreciousMetalOrder create(Long customerId, String metalType, String tradeType,
                                             String direction, BigDecimal weight, String weightUnit,
                                             BigDecimal purity, BigDecimal unitPrice, String currency,
                                             String deliveryType, LocalDate valueDate,
                                             String deliveryLocation) {
        PreciousMetalOrder order = new PreciousMetalOrder();
        order.customerId = customerId;
        order.metalType = metalType;
        order.tradeType = tradeType;
        order.direction = direction;
        order.weight = weight;
        order.weightUnit = weightUnit;
        order.purity = purity;
        order.unitPrice = unitPrice;
        order.currency = currency;
        order.deliveryType = deliveryType;
        order.valueDate = valueDate;
        order.deliveryLocation = deliveryLocation;
        order.calculateTotalAmount();
        order.orderStatus = "PENDING";
        order.createTime = LocalDateTime.now();
        order.validate();
        return order;
    }

    public static PreciousMetalOrder reconstitute(Long id, String orderNo, Long customerId,
                                                   String metalType, String tradeType,
                                                   String direction, BigDecimal weight,
                                                   String weightUnit, BigDecimal purity,
                                                   BigDecimal unitPrice, BigDecimal totalAmount,
                                                   String currency, String settleCurrency,
                                                   String deliveryType, LocalDate valueDate,
                                                   String deliveryLocation, String storageLocation,
                                                   String orderStatus, String riskLevel,
                                                   LocalDateTime createTime) {
        PreciousMetalOrder order = new PreciousMetalOrder();
        order.id = id;
        order.orderNo = orderNo;
        order.customerId = customerId;
        order.metalType = metalType;
        order.tradeType = tradeType;
        order.direction = direction;
        order.weight = weight;
        order.weightUnit = weightUnit;
        order.purity = purity;
        order.unitPrice = unitPrice;
        order.totalAmount = totalAmount;
        order.currency = currency;
        order.settleCurrency = settleCurrency;
        order.deliveryType = deliveryType;
        order.valueDate = valueDate;
        order.deliveryLocation = deliveryLocation;
        order.storageLocation = storageLocation;
        order.orderStatus = orderStatus;
        order.riskLevel = riskLevel;
        order.createTime = createTime;
        return order;
    }

    public void calculateTotalAmount() {
        this.totalAmount = this.weight.multiply(this.unitPrice).multiply(this.purity);
    }

    public void confirm() {
        if (!"PENDING".equals(this.orderStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "仅待确认状态的订单可确认");
        }
        this.orderStatus = "CONFIRMED";
        markUpdated();
    }

    public void execute() {
        if (!"CONFIRMED".equals(this.orderStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "仅已确认状态的订单可执行");
        }
        this.orderStatus = "EXECUTED";
        markUpdated();
    }

    public void settle(String settleCurrency, BigDecimal settleAmount) {
        if (!"EXECUTED".equals(this.orderStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "仅已执行状态的订单可结算");
        }
        this.settleCurrency = settleCurrency;
        this.orderStatus = "SETTLED";
        markUpdated();
    }

    public void cancel() {
        if ("SETTLED".equals(this.orderStatus)) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "已结算订单不可取消");
        }
        this.orderStatus = "CANCELLED";
        markUpdated();
    }

    public void assignOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void assignStorage(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public void evaluateRisk(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    @Override
    protected void validate() {
        if (customerId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "客户ID不能为空");
        }
        if (metalType == null || metalType.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "贵金属类型不能为空");
        }
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "重量必须大于0");
        }
        if (purity == null || purity.compareTo(BigDecimal.ZERO) <= 0 || purity.compareTo(BigDecimal.ONE) > 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "纯度需在0到1之间");
        }
    }
}
