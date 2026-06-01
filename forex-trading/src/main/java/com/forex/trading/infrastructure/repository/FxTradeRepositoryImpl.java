package com.forex.trading.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.trading.domain.model.aggregate.FxTrade;
import com.forex.trading.domain.model.query.TradeQuery;
import com.forex.trading.domain.repository.FxTradeRepository;
import com.forex.trading.infrastructure.mapper.FxTradeMapper;
import com.forex.trading.infrastructure.persistence.FxTradePO;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FxTradeRepositoryImpl implements FxTradeRepository {

    private final FxTradeMapper fxTradeMapper;

    @Override
    public FxTrade save(FxTrade trade) {
        FxTradePO po = toPO(trade);
        if (trade.getId() == null) {
            fxTradeMapper.insert(po);
        } else {
            fxTradeMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<FxTrade> findById(Long id) {
        FxTradePO po = fxTradeMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<FxTrade> findByTradeNo(String tradeNo) {
        FxTradePO po = fxTradeMapper.selectByTradeNo(tradeNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<FxTrade> findByCustomerId(Long customerId) {
        List<FxTradePO> poList = fxTradeMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FxTradePO>()
                        .eq(FxTradePO::getCustomerId, customerId)
                        .orderByDesc(FxTradePO::getCreateTime));
        return poList.stream().map(this::toDomain).toList();
    }

    @Override
    public PageResp<FxTrade> pageQuery(TradeQuery query) {
        IPage<FxTradePO> page = fxTradeMapper.pageQuery(
                new Page<>(query.getPageNum(), query.getPageSize()), query);

        List<FxTrade> records = page.getRecords().stream().map(this::toDomain).toList();
        return PageResp.of(page.getTotal(), records, (int) page.getCurrent(), (int) page.getSize());
    }

    private FxTrade toDomain(FxTradePO po) {
        return FxTrade.reconstitute(
                po.getId(),
                po.getTradeNo(),
                po.getCustomerId(),
                po.getTradeType(),
                po.getDealType(),
                po.getBuyCurrency(),
                po.getSellCurrency(),
                po.getBuyAmount(),
                po.getSellAmount(),
                po.getTradeRate(),
                po.getValueDate(),
                po.getMaturityDate(),
                po.getNearValueDate(),
                po.getFarValueDate(),
                po.getNearRate(),
                po.getFarRate(),
                po.getSwapPoints(),
                po.getOptionType(),
                po.getStrikePrice(),
                po.getPremiumAmount(),
                po.getPremiumCurrency(),
                po.getPremiumDate(),
                po.getExpiryDate(),
                po.getDeliveryType(),
                po.getTradeStatus(),
                po.getSettlementStatus(),
                po.getNostroAccount(),
                po.getCounterparty(),
                po.getTradeChannel(),
                po.getOperatorId(),
                po.getConfirmTime(),
                po.getExecuteTime(),
                po.getSettleTime(),
                po.getRemark()
        );
    }

    private FxTradePO toPO(FxTrade trade) {
        FxTradePO po = new FxTradePO();
        po.setId(trade.getId());
        po.setTradeNo(trade.getTradeNo());
        po.setCustomerId(trade.getCustomerId());
        po.setTradeType(trade.getTradeType());
        po.setDealType(trade.getDealType());
        po.setBuyCurrency(trade.getBuyCurrency());
        po.setSellCurrency(trade.getSellCurrency());
        po.setBuyAmount(trade.getBuyAmount());
        po.setSellAmount(trade.getSellAmount());
        po.setTradeRate(trade.getTradeRate());
        po.setValueDate(trade.getValueDate());
        po.setMaturityDate(trade.getMaturityDate());
        po.setNearValueDate(trade.getNearValueDate());
        po.setFarValueDate(trade.getFarValueDate());
        po.setNearRate(trade.getNearRate());
        po.setFarRate(trade.getFarRate());
        po.setSwapPoints(trade.getSwapPoints());
        po.setOptionType(trade.getOptionType());
        po.setStrikePrice(trade.getStrikePrice());
        po.setPremiumAmount(trade.getPremiumAmount());
        po.setPremiumCurrency(trade.getPremiumCurrency());
        po.setPremiumDate(trade.getPremiumDate());
        po.setExpiryDate(trade.getExpiryDate());
        po.setDeliveryType(trade.getDeliveryType());
        po.setTradeStatus(trade.getTradeStatus());
        po.setSettlementStatus(trade.getSettlementStatus());
        po.setNostroAccount(trade.getNostroAccount());
        po.setCounterparty(trade.getCounterparty());
        po.setTradeChannel(trade.getTradeChannel());
        po.setOperatorId(trade.getOperatorId());
        po.setConfirmTime(trade.getConfirmTime());
        po.setExecuteTime(trade.getExecuteTime());
        po.setSettleTime(trade.getSettleTime());
        po.setRemark(trade.getRemark());
        po.setVersion(trade.getVersion());
        return po;
    }
}
