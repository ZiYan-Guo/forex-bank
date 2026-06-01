package com.forex.exchange.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.exchange.domain.model.query.ExchangeOrderQuery;
import com.forex.exchange.domain.model.aggregate.ExchangeOrder;
import com.forex.exchange.domain.repository.ExchangeOrderRepository;
import com.forex.exchange.infrastructure.mapper.ExchangeOrderMapper;
import com.forex.exchange.infrastructure.persistence.ExchangeOrderPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExchangeOrderRepositoryImpl implements ExchangeOrderRepository {

    private final ExchangeOrderMapper exchangeOrderMapper;

    @Override
    public ExchangeOrder save(ExchangeOrder order) {
        ExchangeOrderPO po = toPO(order);
        if (order.getId() == null) {
            exchangeOrderMapper.insert(po);
        } else {
            exchangeOrderMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ExchangeOrder> findById(Long id) {
        ExchangeOrderPO po = exchangeOrderMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ExchangeOrder> findByOrderNo(String orderNo) {
        ExchangeOrderPO po = exchangeOrderMapper.selectByOrderNo(orderNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<ExchangeOrder> findByCustomerId(Long customerId) {
        return exchangeOrderMapper.selectByCustomerId(customerId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public PageResp<ExchangeOrder> pageQuery(ExchangeOrderQuery query) {
        Page<ExchangeOrderPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<ExchangeOrderPO> result = exchangeOrderMapper.pageQuery(page, query);
        List<ExchangeOrder> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private ExchangeOrder toDomain(ExchangeOrderPO po) {
        return ExchangeOrder.reconstitute(
                po.getId(),
                po.getOrderNo(),
                po.getCustomerId(),
                po.getOrderType(),
                po.getDealType(),
                po.getBaseCurrency(),
                po.getQuoteCurrency(),
                po.getOrderAmount(),
                po.getSettleAmount(),
                po.getBidRate(),
                po.getAskRate(),
                po.getConfirmedRate(),
                po.getRateType(),
                po.getLockRateTime(),
                po.getLockRateExpireTime(),
                po.getValueDate(),
                po.getMaturityDate(),
                po.getOrderStatus(),
                po.getCustomerAccountNo(),
                po.getBankAccountNo(),
                po.getFeeAmount(),
                po.getCommissionAmount(),
                po.getSettlementType(),
                po.getChannel(),
                po.getOperatorId(),
                po.getRemark()
        );
    }

    private ExchangeOrderPO toPO(ExchangeOrder order) {
        ExchangeOrderPO po = new ExchangeOrderPO();
        po.setId(order.getId());
        po.setOrderNo(order.getOrderNo());
        po.setCustomerId(order.getCustomerId());
        po.setOrderType(order.getOrderType());
        po.setDealType(order.getDealType());
        po.setBaseCurrency(order.getBaseCurrency());
        po.setQuoteCurrency(order.getQuoteCurrency());
        po.setOrderAmount(order.getOrderAmount());
        po.setSettleAmount(order.getSettleAmount());
        po.setBidRate(order.getBidRate());
        po.setAskRate(order.getAskRate());
        po.setConfirmedRate(order.getConfirmedRate());
        po.setRateType(order.getRateType());
        po.setLockRateTime(order.getLockRateTime());
        po.setLockRateExpireTime(order.getLockRateExpireTime());
        po.setValueDate(order.getValueDate());
        po.setMaturityDate(order.getMaturityDate());
        po.setOrderStatus(order.getOrderStatus());
        po.setCustomerAccountNo(order.getCustomerAccountNo());
        po.setBankAccountNo(order.getBankAccountNo());
        po.setFeeAmount(order.getFeeAmount());
        po.setCommissionAmount(order.getCommissionAmount());
        po.setSettlementType(order.getSettlementType());
        po.setChannel(order.getChannel());
        po.setOperatorId(order.getOperatorId());
        po.setRemark(order.getRemark());
        return po;
    }
}
