package com.forex.preciousmetal.application.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import com.forex.preciousmetal.application.command.CreateOrderCmd;
import com.forex.preciousmetal.application.command.SettleOrderCmd;
import com.forex.preciousmetal.application.query.OrderQuery;
import com.forex.preciousmetal.domain.model.aggregate.PreciousMetalOrder;
import com.forex.preciousmetal.domain.repository.PreciousMetalOrderRepository;
import com.forex.preciousmetal.domain.service.PreciousMetalDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MetalAppService {

    private final PreciousMetalDomainService domainService;
    private final PreciousMetalOrderRepository orderRepository;

    public String createOrder(CreateOrderCmd cmd) {
        PreciousMetalOrder order = domainService.createOrder(
                cmd.getCustomerId(), cmd.getMetalType(), cmd.getTradeType(),
                cmd.getDirection(), cmd.getWeight(), cmd.getWeightUnit(),
                cmd.getPurity(), cmd.getUnitPrice(), cmd.getCurrency(),
                cmd.getDeliveryType(), cmd.getValueDate(), cmd.getDeliveryLocation());
        return order.getOrderNo();
    }

    public void confirmOrder(String orderNo) {
        PreciousMetalOrder order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "订单不存在"));
        order.confirm();
        orderRepository.save(order);
    }

    public void executeOrder(String orderNo) {
        PreciousMetalOrder order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "订单不存在"));
        order.execute();
        orderRepository.save(order);
    }

    public void cancelOrder(String orderNo) {
        PreciousMetalOrder order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "订单不存在"));
        order.cancel();
        orderRepository.save(order);
    }

    public void settleOrder(SettleOrderCmd cmd) {
        domainService.settleOrder(cmd.getOrderNo(), cmd.getSettleCurrency(), cmd.getSettleAmount());
    }

    public PreciousMetalOrder getOrder(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "订单不存在"));
    }

    public List<PreciousMetalOrder> pageOrders(OrderQuery query) {
        return orderRepository.findPage(query);
    }

    public void markToMarket(Long customerId, String metalType, BigDecimal price) {
        domainService.markToMarket(customerId, metalType, price);
    }
}
