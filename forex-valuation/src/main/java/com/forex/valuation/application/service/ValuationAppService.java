package com.forex.valuation.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.valuation.application.command.CalculateValuationCmd;
import com.forex.valuation.domain.model.aggregate.ValuationResult;
import com.forex.valuation.domain.model.query.ValuationQuery;
import com.forex.valuation.domain.repository.ValuationResultRepository;
import com.forex.valuation.domain.service.ValuationDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValuationAppService {

    private final ValuationResultRepository valuationResultRepository;
    private final ValuationDomainService valuationDomainService;

    public ValuationResult calculateValuation(Long tradeId) {
        ValuationResult input = ValuationResult.create(
                tradeId, "TRADE-" + tradeId, "SPOT",
                LocalDate.now(), "USD/CNY",
                java.math.BigDecimal.valueOf(100000), "GK");
        ValuationResult result = valuationDomainService.calculateValuation(input);
        return valuationDomainService.saveResult(result);
    }

    public List<ValuationResult> getTradeValuations(Long tradeId) {
        return valuationResultRepository.findByTradeId(tradeId);
    }

    public PageResp<ValuationResult> pageQuery(ValuationQuery query) {
        return valuationResultRepository.pageQuery(query);
    }

    public List<ValuationResult> recalculateAll(LocalDate date) {
        return getTradeValuations(null);
    }
}
