package com.forex.trading.domain.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.trading.domain.event.TradeExecutedEvent;
import com.forex.trading.domain.event.TradeSettledEvent;
import com.forex.trading.domain.model.aggregate.FxTrade;
import com.forex.trading.domain.repository.FxTradeRepository;
import com.forex.trading.domain.model.valueobject.SwapPoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TradeDomainService {

    private final FxTradeRepository fxTradeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public FxTrade createTrade(FxTrade trade) {
        FxTrade saved = fxTradeRepository.save(trade);
        log.info("Created fx trade: {}", saved.getTradeNo());
        return saved;
    }

    public void confirmTrade(FxTrade trade) {
        trade.confirm();
        fxTradeRepository.save(trade);
        log.info("Confirmed fx trade: {}", trade.getTradeNo());
    }

    public void executeTrade(FxTrade trade) {
        trade.execute();
        fxTradeRepository.save(trade);
        eventPublisher.publishEvent(new TradeExecutedEvent(
                trade.getId(), trade.getTradeNo(), trade.getTradeType(),
                trade.getBuyAmount(), trade.getBuyCurrency(), trade.getSellCurrency()));
        log.info("Executed fx trade: {}", trade.getTradeNo());
    }

    public void settleTrade(FxTrade trade) {
        trade.settle();
        fxTradeRepository.save(trade);
        eventPublisher.publishEvent(new TradeSettledEvent(
                trade.getId(), trade.getTradeNo(), trade.getSettlementStatus()));
        log.info("Settled fx trade: {}", trade.getTradeNo());
    }

    public void rollOverTrade(FxTrade trade, LocalDate newDate, BigDecimal newRate) {
        trade.rollOver(newDate, newRate);
        fxTradeRepository.save(trade);
        log.info("Rolled over fx trade: {}, newDate: {}, newRate: {}", trade.getTradeNo(), newDate, newRate);
    }

    public void closeOutTrade(FxTrade trade) {
        trade.closeOut();
        fxTradeRepository.save(trade);
        log.info("Closed out fx trade: {}", trade.getTradeNo());
    }

    public void cancelTrade(FxTrade trade, String reason) {
        trade.cancel(reason);
        fxTradeRepository.save(trade);
        log.info("Cancelled fx trade: {}, reason: {}", trade.getTradeNo(), reason);
    }

    public BigDecimal calculateForwardRate(BigDecimal spotRate, SwapPoints points) {
        if (spotRate == null) {
            throw new BusinessException("即期汇率不能为空");
        }
        if (points == null) {
            throw new BusinessException("掉期点不能为空");
        }
        return points.applyToRate(spotRate);
    }

    public BigDecimal calculateSwapAmount(FxTrade trade) {
        if (trade == null) {
            throw new BusinessException("交易不能为空");
        }
        if (trade.getBuyAmount() == null || trade.getSellAmount() == null) {
            return BigDecimal.ZERO;
        }
        return trade.getBuyAmount().subtract(trade.getSellAmount());
    }
}
