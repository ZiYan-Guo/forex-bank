package com.forex.saccr.domain.service;

import com.forex.saccr.domain.model.aggregate.SaccrResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaccrCalculationService {

    private static final BigDecimal ALPHA = new BigDecimal("1.4");

    /**
     * Calculate SA-CCR exposure. SA-CCR 标准法计算风险敞口。
     * Exposure = Alpha × (RC + PFE)
     */
    public SaccrResult calculate(SaccrResult input) {
        BigDecimal rc = calculateRC(input);
        BigDecimal pfe = calculatePFE(input);
        BigDecimal exposure = ALPHA.multiply(rc.add(pfe)).setScale(2, RoundingMode.HALF_UP);
        input.updateResult(rc, pfe);
        return input;
    }

    private BigDecimal calculateRC(SaccrResult result) {
        return result.getRc() != null ? result.getRc() : BigDecimal.ZERO;
    }

    private BigDecimal calculatePFE(SaccrResult result) {
        return result.getPfe() != null ? result.getPfe() : BigDecimal.ZERO;
    }
}
