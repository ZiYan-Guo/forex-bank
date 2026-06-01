package com.forex.valuation.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.valuation.domain.model.aggregate.ValuationResult;
import com.forex.valuation.domain.model.query.ValuationQuery;
import com.forex.valuation.domain.repository.ValuationResultRepository;
import com.forex.valuation.infrastructure.mapper.ValuationResultMapper;
import com.forex.valuation.infrastructure.persistence.ValuationResultPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ValuationResultRepositoryImpl implements ValuationResultRepository {

    private final ValuationResultMapper valuationResultMapper;

    @Override
    public ValuationResult save(ValuationResult valuationResult) {
        ValuationResultPO po = toPO(valuationResult);
        if (valuationResult.getId() == null) {
            valuationResultMapper.insert(po);
        } else {
            valuationResultMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ValuationResult> findById(Long id) {
        ValuationResultPO po = valuationResultMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<ValuationResult> findByTradeId(Long tradeId) {
        return valuationResultMapper.selectByTradeId(tradeId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public PageResp<ValuationResult> pageQuery(ValuationQuery query) {
        Page<ValuationResultPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<ValuationResultPO> result = valuationResultMapper.pageQuery(page, query);
        List<ValuationResult> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private ValuationResult toDomain(ValuationResultPO po) {
        return ValuationResult.reconstitute(
                po.getId(),
                po.getTradeId(),
                po.getTradeNo(),
                po.getTradeType(),
                po.getValuationDate(),
                po.getCurrencyPair(),
                po.getNotionalAmount(),
                po.getFairValue(),
                po.getPnl(),
                po.getCumulativePnl(),
                po.getValuationMethod(),
                po.getModelParams(),
                po.getMarketDataSnapshot()
        );
    }

    private ValuationResultPO toPO(ValuationResult valuationResult) {
        ValuationResultPO po = new ValuationResultPO();
        po.setId(valuationResult.getId());
        po.setTradeId(valuationResult.getTradeId());
        po.setTradeNo(valuationResult.getTradeNo());
        po.setTradeType(valuationResult.getTradeType());
        po.setValuationDate(valuationResult.getValuationDate());
        po.setCurrencyPair(valuationResult.getCurrencyPair());
        po.setNotionalAmount(valuationResult.getNotionalAmount());
        po.setFairValue(valuationResult.getFairValue());
        po.setPnl(valuationResult.getPnl());
        po.setCumulativePnl(valuationResult.getCumulativePnl());
        po.setValuationMethod(valuationResult.getValuationMethod());
        po.setModelParams(valuationResult.getModelParams());
        po.setMarketDataSnapshot(valuationResult.getMarketDataSnapshot());
        return po;
    }
}
