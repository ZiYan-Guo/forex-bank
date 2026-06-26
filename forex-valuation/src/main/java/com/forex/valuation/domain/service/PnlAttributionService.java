package com.forex.valuation.domain.service;

import com.forex.valuation.domain.model.aggregate.PnlAttribution;
import com.forex.valuation.domain.model.valueobject.ValuationInput;
import com.forex.valuation.domain.model.valueobject.ValuationModelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PnlAttributionService {

    private final ValuationModelRegistry modelRegistry;

    public PnlAttribution calculateAttribution(ValuationInput todayInput, ValuationInput yesterdayInput,
                                                ValuationModelType modelType) {
        ValuationModel model = modelRegistry.getModel(modelType);

        BigDecimal todayFairValue = model.calculateFairValue(todayInput);
        BigDecimal yesterdayFairValue = model.calculateFairValue(yesterdayInput);
        BigDecimal yesterdayDelta = model.calculateDelta(yesterdayInput);
        BigDecimal yesterdayGamma = model.calculateGamma(yesterdayInput);
        BigDecimal yesterdayVega = model.calculateVega(yesterdayInput);
        BigDecimal yesterdayTheta = model.calculateTheta(yesterdayInput);

        BigDecimal spotDiff = todayInput.getSpotRate().subtract(yesterdayInput.getSpotRate());
        BigDecimal deltaPnl = yesterdayDelta.multiply(spotDiff);

        BigDecimal thetaPnl = yesterdayTheta;

        BigDecimal spotDiffSquared = spotDiff.pow(2);
        BigDecimal gammaPnl = BigDecimal.valueOf(0.5)
                .multiply(yesterdayGamma)
                .multiply(spotDiffSquared)
                .multiply(todayInput.getNotionalAmount());

        BigDecimal volDiff = todayInput.getVolatility().subtract(yesterdayInput.getVolatility());
        BigDecimal vegaPnl = yesterdayVega.multiply(volDiff);

        double rf = todayInput.getForeignRate().doubleValue();
        double rd = todayInput.getDomesticRate().doubleValue();
        double notional = todayInput.getNotionalAmount().doubleValue();
        BigDecimal carryPnl = BigDecimal.valueOf((rf - rd) * notional / 365.0);

        PnlAttribution attr = PnlAttribution.create(
                todayInput.getTradeId(), "TRADE-" + todayInput.getTradeId(),
                todayInput.getValuationDate(), "MODEL", modelType.getCode());
        attr.populatePnlComponents(deltaPnl, thetaPnl, gammaPnl, vegaPnl, carryPnl, BigDecimal.ZERO);

        log.info("Calculated P&L attribution for tradeId: {}, totalPnl: {}",
                todayInput.getTradeId(), attr.getTotalPnl());
        return attr;
    }
}
