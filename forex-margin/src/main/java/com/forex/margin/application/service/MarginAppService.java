package com.forex.margin.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.margin.application.command.CreateMarginCmd;
import com.forex.margin.domain.model.aggregate.MarginAccount;
import com.forex.margin.domain.model.query.MarginQuery;
import com.forex.margin.domain.repository.MarginAccountRepository;
import com.forex.margin.domain.service.MarginDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Service
@RequiredArgsConstructor
public class MarginAppService {

    private final MarginAccountRepository marginAccountRepository;
    private final MarginDomainService marginDomainService;

    @Transactional
    public MarginAccount createInitialMargin(Long customerId, Long tradeId, BigDecimal notional, BigDecimal rate) {
        BigDecimal requiredAmount = marginDomainService.calculateInitialMargin(notional, rate);
        MarginAccount account = marginDomainService.createMargin(customerId, tradeId, "INITIAL",
                "USD", requiredAmount, rate, "CASH");
        return marginAccountRepository.save(account);
    }

    @Transactional
    public MarginAccount callMargin(String marginNo, BigDecimal amount) {
        MarginAccount account = marginAccountRepository.findByMarginNo(marginNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "保证金账户不存在"));
        return marginDomainService.callMargin(account, amount);
    }

    @Transactional
    public MarginAccount releaseMargin(String marginNo, BigDecimal amount, String reason) {
        MarginAccount account = marginAccountRepository.findByMarginNo(marginNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "保证金账户不存在"));
        return marginDomainService.releaseMargin(account, amount, reason);
    }

    @Transactional
    public MarginAccount depositMargin(String marginNo, BigDecimal amount) {
        MarginAccount account = marginAccountRepository.findByMarginNo(marginNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "保证金账户不存在"));
        account.deposit(amount);
        return marginAccountRepository.save(account);
    }

    public MarginAccount getMarginDetail(String marginNo) {
        return marginAccountRepository.findByMarginNo(marginNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "保证金账户不存在"));
    }

    public PageResp<MarginAccount> pageQuery(MarginQuery query) {
        return marginAccountRepository.pageQuery(query);
    }
}
