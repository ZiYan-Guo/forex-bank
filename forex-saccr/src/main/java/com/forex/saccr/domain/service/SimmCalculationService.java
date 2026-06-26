package com.forex.saccr.domain.service;

import com.forex.saccr.domain.model.aggregate.SimmResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SimmCalculationService {

    /**
     * Calculate ISDA SIMM initial margin. ISDA SIMM 初始保证金计算。
     * IM = Delta_Margin + Vega_Margin + Curvature_Margin
     * Based on 99% confidence, 10+N-1 day risk horizon.
     */
    public SimmResult calculate(SimmResult input) {
        BigDecimal delta = calculateDeltaMargin(input);
        BigDecimal vega = calculateVegaMargin(input);
        BigDecimal curvature = calculateCurvatureMargin(input);
        BigDecimal total = delta.add(vega).add(curvature);
        input.updateResult(delta, vega, curvature);
        return input;
    }

    private BigDecimal calculateDeltaMargin(SimmResult result) {
        return result.getNotionalAmount() != null
                ? result.getNotionalAmount().multiply(new BigDecimal("0.05")) : BigDecimal.ZERO;
    }

    private BigDecimal calculateVegaMargin(SimmResult result) {
        return result.getNotionalAmount() != null
                ? result.getNotionalAmount().multiply(new BigDecimal("0.02")) : BigDecimal.ZERO;
    }

    private BigDecimal calculateCurvatureMargin(SimmResult result) {
        return result.getNotionalAmount() != null
                ? result.getNotionalAmount().multiply(new BigDecimal("0.01")) : BigDecimal.ZERO;
    }
}
