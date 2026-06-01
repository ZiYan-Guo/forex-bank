package com.forex.valuation.domain.service;

import com.forex.valuation.domain.event.ValuationCompletedEvent;
import com.forex.valuation.domain.model.aggregate.ValuationResult;
import com.forex.valuation.domain.repository.ValuationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
/** Derivative valuation domain service. Calculates fair value and P&L. 衍生品估值领域服务。 */
public class ValuationDomainService {

    private final ValuationResultRepository valuationResultRepository;
    private final ApplicationEventPublisher eventPublisher;

    /** Calculate valuation for a trade. BS/GK models estimate fair value. 计算交易的估值(BS/GK模型)。 */
    public ValuationResult calculateValuation(ValuationResult input) {
        ValuationResult result = ValuationResult.create(
                input.getTradeId(), input.getTradeNo(), input.getTradeType(),
                input.getValuationDate() != null ? input.getValuationDate() : java.time.LocalDate.now(),
                input.getCurrencyPair(), input.getNotionalAmount(), input.getValuationMethod());
        BigDecimal estimatedFairValue = estimateFairValue(result);
        BigDecimal pnl = calculatePnL(result, estimatedFairValue);
        result.recalculate(estimatedFairValue, pnl);
        log.info("Calculated valuation for tradeId: {}, fairValue: {}, pnl: {}",
                input.getTradeId(), estimatedFairValue, pnl);
        return result;
    }

    /** Estimate fair value based on valuation method. 根据估值方法估算公允价值。 */
    private BigDecimal estimateFairValue(ValuationResult result) {
        if ("BS".equalsIgnoreCase(result.getValuationMethod())
                || "GK".equalsIgnoreCase(result.getValuationMethod())) {
            return result.getNotionalAmount() != null
                    ? result.getNotionalAmount().multiply(java.math.BigDecimal.ONE)
                    : java.math.BigDecimal.ZERO;
        }
        return result.getNotionalAmount() != null ? result.getNotionalAmount() : java.math.BigDecimal.ZERO;
    }

    /** Calculate P&L as fairValue minus notionalAmount. 计算损益=公允价值-名义本金。 */
    private BigDecimal calculatePnL(ValuationResult result, BigDecimal fairValue) {
        if (result.getNotionalAmount() == null) return java.math.BigDecimal.ZERO;
        return fairValue.subtract(result.getNotionalAmount());
    }

    public ValuationResult saveResult(ValuationResult valuationResult) {
        ValuationResult saved = valuationResultRepository.save(valuationResult);
        eventPublisher.publishEvent(new ValuationCompletedEvent(
                saved.getTradeId(), saved.getFairValue()));
        log.info("Saved valuation result for tradeId: {}", saved.getTradeId());
        return saved;
    }
}
