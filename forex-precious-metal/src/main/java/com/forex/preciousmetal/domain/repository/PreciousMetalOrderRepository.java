package com.forex.preciousmetal.domain.repository;

import com.forex.preciousmetal.application.query.OrderQuery;
import com.forex.preciousmetal.domain.model.aggregate.PreciousMetalOrder;

import java.util.List;
import java.util.Optional;

public interface PreciousMetalOrderRepository {
    void save(PreciousMetalOrder order);
    Optional<PreciousMetalOrder> findById(Long id);
    Optional<PreciousMetalOrder> findByOrderNo(String orderNo);
    List<PreciousMetalOrder> findPage(OrderQuery query);
}
