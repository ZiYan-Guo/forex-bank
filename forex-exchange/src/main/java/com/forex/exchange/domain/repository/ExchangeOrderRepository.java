package com.forex.exchange.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.exchange.domain.model.aggregate.ExchangeOrder;
import com.forex.exchange.domain.model.query.ExchangeOrderQuery;

import java.util.List;
import java.util.Optional;

public interface ExchangeOrderRepository {

    ExchangeOrder save(ExchangeOrder order);

    Optional<ExchangeOrder> findById(Long id);

    Optional<ExchangeOrder> findByOrderNo(String orderNo);

    List<ExchangeOrder> findByCustomerId(Long customerId);

    PageResp<ExchangeOrder> pageQuery(ExchangeOrderQuery query);
}
