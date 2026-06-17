package com.forex.margin.infrastructure.repository;

import com.forex.margin.domain.model.entity.MarginCall;
import com.forex.margin.domain.repository.MarginCallRepository;
import com.forex.margin.infrastructure.mapper.MarginCallMapper;
import com.forex.margin.infrastructure.persistence.MarginCallPO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Margin call repository implementation.
 * 保证金追缴仓储实现。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MarginCallRepositoryImpl implements MarginCallRepository {

    private final MarginCallMapper marginCallMapper;

    @Override
    public void save(MarginCall marginCall) {
        MarginCallPO po = toPO(marginCall);
        if (marginCall.getId() == null) {
            marginCallMapper.insert(po);
            log.info("Margin call created: marginNo={}, callType={}, amount={}", po.getMarginNo(), po.getCallType(), po.getCallAmount());
        } else {
            marginCallMapper.updateById(po);
            log.info("Margin call updated: id={}, responseStatus={}", po.getId(), po.getResponseStatus());
        }
    }

    @Override
    public MarginCall findById(Long id) {
        MarginCallPO po = marginCallMapper.selectByCallId(id);
        return po != null ? toDomain(po) : null;
    }

    @Override
    public List<MarginCall> findByMarginId(Long marginId) {
        return marginCallMapper.selectByMarginId(marginId).stream()
                .map(this::toDomain)
                .toList();
    }

    private MarginCall toDomain(MarginCallPO po) {
        return new MarginCall(
                po.getId(),
                po.getMarginId(),
                po.getMarginNo(),
                po.getCallType(),
                po.getCallAmount(),
                po.getCallDate(),
                po.getResponseDate(),
                po.getResponseStatus()
        );
    }

    private MarginCallPO toPO(MarginCall call) {
        MarginCallPO po = new MarginCallPO();
        po.setId(call.getId());
        po.setMarginId(call.getMarginId());
        po.setMarginNo(call.getMarginNo());
        po.setCallType(call.getCallType());
        po.setCallAmount(call.getCallAmount());
        po.setCallDate(call.getCallDate());
        po.setResponseDate(call.getResponseDate());
        po.setResponseStatus(call.getResponseStatus());
        return po;
    }
}
