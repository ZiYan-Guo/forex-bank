package com.forex.rate.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Service
@Transactional
public class RateCalculationDomainService {

    public BigDecimal calculateCrossRate(BigDecimal usdBaseRate, BigDecimal usdQuoteRate) {
        if (usdBaseRate == null || usdBaseRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "usdBaseRate must be greater than 0");
        }
        if (usdQuoteRate == null || usdQuoteRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "usdQuoteRate must be greater than 0");
        }
        return usdQuoteRate.divide(usdBaseRate, 8, RoundingMode.HALF_UP);
    }

    public BigDecimal applySpread(BigDecimal baseRate, BigDecimal spreadAdjust) {
        if (baseRate == null || baseRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "baseRate must be greater than 0");
        }
        if (spreadAdjust == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "spreadAdjust must not be null");
        }
        return baseRate.add(spreadAdjust);
    }

    public BigDecimal calculateAmount(BigDecimal amount, BigDecimal rate) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "amount must be greater than 0");
        }
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "rate must be greater than 0");
        }
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
