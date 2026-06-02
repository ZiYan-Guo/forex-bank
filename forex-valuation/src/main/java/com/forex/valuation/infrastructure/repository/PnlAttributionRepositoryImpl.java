package com.forex.valuation.infrastructure.repository;

import com.forex.valuation.domain.model.aggregate.PnlAttribution;
import com.forex.valuation.domain.repository.PnlAttributionRepository;
import com.forex.valuation.infrastructure.mapper.PnlAttributionMapper;
import com.forex.valuation.infrastructure.persistence.PnlAttributionPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PnlAttributionRepositoryImpl implements PnlAttributionRepository {

    private final PnlAttributionMapper pnlAttributionMapper;

    @Override
    public PnlAttribution save(PnlAttribution attribution) {
        PnlAttributionPO po = toPO(attribution);
        if (attribution.getId() == null) {
            pnlAttributionMapper.insert(po);
        } else {
            pnlAttributionMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<PnlAttribution> findById(Long id) {
        PnlAttributionPO po = pnlAttributionMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<PnlAttribution> findByTradeId(Long tradeId) {
        return pnlAttributionMapper.selectByTradeId(tradeId).stream()
                .map(this::toDomain)
                .toList();
    }

    private PnlAttribution toDomain(PnlAttributionPO po) {
        return PnlAttribution.reconstitute(
                po.getId(),
                po.getAttribNo(),
                po.getTradeId(),
                po.getTradeNo(),
                po.getAttribDate(),
                po.getTotalPnl(),
                po.getDeltaPnl(),
                po.getThetaPnl(),
                po.getGammaPnl(),
                po.getVegaPnl(),
                po.getCarryPnl(),
                po.getTradePnl(),
                po.getTariffType(),
                po.getTariffValue()
        );
    }

    private PnlAttributionPO toPO(PnlAttribution attribution) {
        PnlAttributionPO po = new PnlAttributionPO();
        po.setId(attribution.getId());
        po.setAttribNo(attribution.getAttribNo());
        po.setTradeId(attribution.getTradeId());
        po.setTradeNo(attribution.getTradeNo());
        po.setAttribDate(attribution.getAttribDate());
        po.setTotalPnl(attribution.getTotalPnl());
        po.setDeltaPnl(attribution.getDeltaPnl());
        po.setThetaPnl(attribution.getThetaPnl());
        po.setGammaPnl(attribution.getGammaPnl());
        po.setVegaPnl(attribution.getVegaPnl());
        po.setCarryPnl(attribution.getCarryPnl());
        po.setTradePnl(attribution.getTradePnl());
        po.setTariffType(attribution.getTariffType());
        po.setTariffValue(attribution.getTariffValue());
        return po;
    }
}
