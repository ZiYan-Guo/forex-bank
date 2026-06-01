package com.forex.customer.domain.service;

import com.forex.customer.domain.model.entity.CreditLimit;
import com.forex.customer.domain.repository.CreditLimitRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditCheckDomainService {

    private final CreditLimitRepository creditLimitRepository;

    public boolean checkCreditAvailability(Long customerId, String limitType, String currency,
                                            BigDecimal amount) {
        return creditLimitRepository.findByCustomerAndType(customerId, limitType, currency)
                .filter(limit -> limit.hasSufficientLimit(amount))
                .isPresent();
    }

    @Transactional
    public void deductCredit(Long customerId, String limitType, String currency, BigDecimal amount) {
        CreditLimit limit = creditLimitRepository
                .findByCustomerAndType(customerId, limitType, currency)
                .orElseThrow(() -> new IllegalArgumentException("信用额度不存在"));

        limit.deduct(amount);
        creditLimitRepository.save(limit);

        log.info("信用额度扣减: customerId={}, limitType={}, currency={}, amount={}",
                customerId, limitType, currency, amount);
    }

    @Transactional
    public void releaseCredit(Long customerId, String limitType, String currency, BigDecimal amount) {
        CreditLimit limit = creditLimitRepository
                .findByCustomerAndType(customerId, limitType, currency)
                .orElseThrow(() -> new IllegalArgumentException("信用额度不存在"));

        limit.release(amount);
        creditLimitRepository.save(limit);

        log.info("信用额度释放: customerId={}, limitType={}, currency={}, amount={}",
                customerId, limitType, currency, amount);
    }
}
