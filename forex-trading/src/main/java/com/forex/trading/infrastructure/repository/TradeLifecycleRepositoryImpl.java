package com.forex.trading.infrastructure.repository;

import com.forex.trading.domain.model.entity.TradeLifecycle;
import com.forex.trading.domain.repository.TradeLifecycleRepository;
import com.forex.trading.infrastructure.mapper.TradeLifecycleMapper;
import com.forex.trading.infrastructure.persistence.TradeLifecyclePO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeLifecycleRepositoryImpl implements TradeLifecycleRepository {

    private final TradeLifecycleMapper tradeLifecycleMapper;

    @Override
    public TradeLifecycle save(TradeLifecycle event) {
        TradeLifecyclePO po = toPO(event);
        if (event.getId() == null) {
            tradeLifecycleMapper.insert(po);
        } else {
            tradeLifecycleMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public List<TradeLifecycle> findByTradeId(Long tradeId) {
        List<TradeLifecyclePO> poList = tradeLifecycleMapper.selectByTradeId(tradeId);
        return poList.stream().map(this::toDomain).toList();
    }

    private TradeLifecycle toDomain(TradeLifecyclePO po) {
        return new TradeLifecycle(
                po.getId(),
                po.getTradeId(),
                po.getTradeNo(),
                po.getEventType(),
                po.getEventTime(),
                po.getBeforeStatus(),
                po.getAfterStatus(),
                po.getEventData(),
                po.getOperatorId()
        );
    }

    private TradeLifecyclePO toPO(TradeLifecycle event) {
        TradeLifecyclePO po = new TradeLifecyclePO();
        po.setId(event.getId());
        po.setTradeId(event.getTradeId());
        po.setTradeNo(event.getTradeNo());
        po.setEventType(event.getEventType());
        po.setEventTime(event.getEventTime());
        po.setBeforeStatus(event.getBeforeStatus());
        po.setAfterStatus(event.getAfterStatus());
        po.setEventData(event.getEventData());
        po.setOperatorId(event.getOperatorId());
        return po;
    }
}
