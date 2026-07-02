package com.forex.limit.application.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import com.forex.limit.application.command.CreateLimitCmd;
import com.forex.limit.domain.model.aggregate.LimitConfig;
import com.forex.limit.domain.repository.LimitConfigRepository;
import com.forex.limit.domain.service.LimitDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LimitAppService {

    private final LimitDomainService domainService;
    private final LimitConfigRepository limitConfigRepository;

    public String createLimit(CreateLimitCmd cmd) {
        LimitConfig config = domainService.createLimit(
                cmd.getCustomerId(), cmd.getLimitType(), cmd.getDimension(),
                cmd.getDimensionValue(), cmd.getLimitAmount(), cmd.getCurrency(),
                cmd.getLimitPeriod(), cmd.getEffectiveDate(), cmd.getExpiryDate());
        return config.getLimitNo();
    }

    public void approveLimit(String limitNo) {
        LimitConfig config = limitConfigRepository.findByLimitNo(limitNo)
                .orElseThrow(() -> new BusinessException(ResultCode.VALIDATE_FAIL, "限额配置不存在"));
        config.approve();
        limitConfigRepository.save(config);
    }

    public boolean checkLimit(Long customerId, String limitType, String dimension,
                               String dimensionValue, BigDecimal amount) {
        return domainService.checkLimit(customerId, limitType, dimension, dimensionValue, amount);
    }

    public List<LimitConfig> getCustomerLimits(Long customerId) {
        return limitConfigRepository.findByCustomer(customerId);
    }
}
