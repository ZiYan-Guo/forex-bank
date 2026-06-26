package com.forex.trading.application.service;

import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.trading.application.command.CreateTradeCmd;
import com.forex.trading.domain.model.aggregate.FxTrade;
import com.forex.trading.domain.model.query.TradeQuery;
import com.forex.trading.domain.repository.FxTradeRepository;
import com.forex.trading.domain.service.TradeDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Service
@RequiredArgsConstructor
public class TradingAppService {

    private final FxTradeRepository fxTradeRepository;
    private final TradeDomainService tradeDomainService;

    @Transactional
    public FxTrade createSpotTrade(CreateTradeCmd cmd) {
        return doCreateTrade(cmd);
    }

    @Transactional
    public FxTrade createForwardTrade(CreateTradeCmd cmd) {
        return doCreateTrade(cmd);
    }

    @Transactional
    public FxTrade createSwapTrade(CreateTradeCmd cmd) {
        return doCreateTrade(cmd);
    }

    @Transactional
    public FxTrade createOptionTrade(CreateTradeCmd cmd) {
        return doCreateTrade(cmd);
    }

    public FxTrade getTradeDetail(String tradeNo) {
        return fxTradeRepository.findByTradeNo(tradeNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "交易不存在"));
    }

    public PageResp<FxTrade> pageQuery(TradeQuery query) {
        return fxTradeRepository.pageQuery(query);
    }

    @RedisLock(key = "#tradeNo")
    @Transactional
    public void confirmTrade(String tradeNo) {
        FxTrade trade = getTradeDetail(tradeNo);
        tradeDomainService.confirmTrade(trade);
    }

    @RedisLock(key = "#tradeNo")
    @Transactional
    public void executeTrade(String tradeNo) {
        FxTrade trade = getTradeDetail(tradeNo);
        tradeDomainService.executeTrade(trade);
    }

    @RedisLock(key = "#tradeNo")
    @Transactional
    public void settleTrade(String tradeNo) {
        FxTrade trade = getTradeDetail(tradeNo);
        tradeDomainService.settleTrade(trade);
    }

    @RedisLock(key = "#tradeNo")
    @Transactional
    public void rollOverTrade(String tradeNo, LocalDate newDate, BigDecimal newRate) {
        FxTrade trade = getTradeDetail(tradeNo);
        tradeDomainService.rollOverTrade(trade, newDate, newRate);
    }

    @RedisLock(key = "#tradeNo")
    @Transactional
    public void closeOutTrade(String tradeNo) {
        FxTrade trade = getTradeDetail(tradeNo);
        tradeDomainService.closeOutTrade(trade);
    }

    @RedisLock(key = "#tradeNo")
    @Transactional
    public void cancelTrade(String tradeNo, String reason) {
        FxTrade trade = getTradeDetail(tradeNo);
        tradeDomainService.cancelTrade(trade, reason);
    }

    private FxTrade doCreateTrade(CreateTradeCmd cmd) {
        String tradeNo = UUID.randomUUID().toString().replace("-", "");
        FxTrade trade = FxTrade.create(
                tradeNo,
                cmd.getCustomerId(),
                cmd.getTradeType(),
                cmd.getDealType(),
                cmd.getBuyCurrency(),
                cmd.getSellCurrency(),
                cmd.getBuyAmount(),
                cmd.getSellAmount(),
                cmd.getTradeRate(),
                cmd.getValueDate(),
                cmd.getTradeChannel(),
                null
        );
        return tradeDomainService.createTrade(trade);
    }
}
