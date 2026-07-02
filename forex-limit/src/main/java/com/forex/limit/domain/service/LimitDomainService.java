package com.forex.limit.domain.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import com.forex.limit.domain.model.aggregate.LimitConfig;
import com.forex.limit.domain.repository.LimitConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LimitDomainService {

    private final LimitConfigRepository limitConfigRepository;

    public LimitConfig createLimit(Long customerId, String limitType, String dimension,
                                    String dimensionValue, BigDecimal limitAmount, String currency,
                                    String limitPeriod, LocalDate effectiveDate, LocalDate expiryDate) {
        List<LimitConfig> existing = limitConfigRepository.findActiveByCustomer(customerId, limitType, dimension, dimensionValue);
        for (LimitConfig c : existing) {
            if ("ACTIVE".equals(c.getLimitStatus()) && isOverlapping(c, effectiveDate, expiryDate)) {
                throw new BusinessException(ResultCode.VALIDATE_FAIL,
                        "已存在重叠期限的限额配置: " + c.getLimitNo());
            }
        }

        LimitConfig config = LimitConfig.create(customerId, limitType, dimension,
                dimensionValue, limitAmount, currency, limitPeriod, effectiveDate, expiryDate);
        config.assignLimitNo("LM" + System.currentTimeMillis());
        limitConfigRepository.save(config);
        return config;
    }

    public boolean checkLimit(Long customerId, String limitType, String dimension,
                               String dimensionValue, BigDecimal requestAmount) {
        List<LimitConfig> configs = limitConfigRepository.findActiveByCustomer(
                customerId, limitType, dimension, dimensionValue);
        if (configs.isEmpty()) {
            return false;
        }
        for (LimitConfig config : configs) {
            if (!config.isExceeded(requestAmount)) {
                return true;
            }
        }
        return false;
    }

    public void utilizeLimit(Long customerId, String limitType, String dimension,
                              String dimensionValue, BigDecimal amount) {
        List<LimitConfig> configs = limitConfigRepository.findActiveByCustomer(
                customerId, limitType, dimension, dimensionValue);
        for (LimitConfig config : configs) {
            if (!config.isExceeded(amount)) {
                config.utilize(amount);
                limitConfigRepository.save(config);
                return;
            }
        }
        throw new BusinessException(ResultCode.VALIDATE_FAIL, "所有限额均不足");
    }

    private boolean isOverlapping(LimitConfig config, LocalDate start, LocalDate end) {
        return config.getEffectiveDate() != null && config.getExpiryDate() != null
                && !config.getExpiryDate().isBefore(start)
                && !config.getEffectiveDate().isAfter(end);
    }
}
