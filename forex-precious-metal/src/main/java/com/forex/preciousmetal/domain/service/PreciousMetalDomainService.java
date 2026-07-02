package com.forex.preciousmetal.domain.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import com.forex.preciousmetal.domain.model.aggregate.MetalPosition;
import com.forex.preciousmetal.domain.model.aggregate.PreciousMetalOrder;
import com.forex.preciousmetal.domain.repository.MetalPositionRepository;
import com.forex.preciousmetal.domain.repository.PreciousMetalOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PreciousMetalDomainService {

    private final PreciousMetalOrderRepository orderRepository;
    private final MetalPositionRepository positionRepository;

    public PreciousMetalOrder createOrder(Long customerId, String metalType, String tradeType,
                                           String direction, BigDecimal weight, String weightUnit,
                                           BigDecimal purity, BigDecimal unitPrice, String currency,
                                           String deliveryType, LocalDate valueDate,
                                           String deliveryLocation) {
        if ("SELL".equals(direction)) {
            MetalPosition position = positionRepository.findByCustomerAndMetal(customerId, metalType)
                    .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "无该贵金属持仓，无法卖出"));
            if (position.getAvailableWeight().compareTo(weight) < 0) {
                throw new BusinessException(ResultCode.VALIDATE_FAIL, "可用库存不足");
            }
            position.lock(weight);
            positionRepository.save(position);
        }

        PreciousMetalOrder order = PreciousMetalOrder.create(
                customerId, metalType, tradeType, direction, weight, weightUnit,
                purity, unitPrice, currency, deliveryType, valueDate, deliveryLocation);

        String orderNo = "PM" + LocalDate.now().toString().replace("-", "")
                + metalType.substring(0, 2).toUpperCase()
                + String.format("%06d", System.currentTimeMillis() % 1000000);
        order.assignOrderNo(orderNo);

        orderRepository.save(order);
        return order;
    }

    public void settleOrder(String orderNo, String settleCurrency, BigDecimal settleAmount) {
        PreciousMetalOrder order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "订单不存在"));

        order.settle(settleCurrency, settleAmount);

        if ("BUY".equals(order.getDirection())) {
            MetalPosition position = positionRepository
                    .findByCustomerAndMetal(order.getCustomerId(), order.getMetalType())
                    .orElseGet(() -> MetalPosition.create(
                            order.getCustomerId(), order.getMetalType(),
                            BigDecimal.ZERO, order.getWeightUnit(),
                            BigDecimal.ZERO, order.getCurrency(),
                            order.getDeliveryLocation()));

            position.addWeight(order.getWeight(), order.getUnitPrice());
            positionRepository.save(position);
        } else {
            MetalPosition position = positionRepository
                    .findByCustomerAndMetal(order.getCustomerId(), order.getMetalType())
                    .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "持仓不存在"));
            position.deliver(order.getWeight());
            positionRepository.save(position);
        }

        orderRepository.save(order);
    }

    public void markToMarket(Long customerId, String metalType, BigDecimal newPrice) {
        MetalPosition position = positionRepository.findByCustomerAndMetal(customerId, metalType)
                .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "持仓不存在"));
        position.markToMarket(newPrice);
        positionRepository.save(position);
    }
}
