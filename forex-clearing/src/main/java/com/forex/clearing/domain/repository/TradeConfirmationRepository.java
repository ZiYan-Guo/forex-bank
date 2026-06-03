package com.forex.clearing.domain.repository;

import com.forex.clearing.domain.model.aggregate.TradeConfirmation;

import java.util.List;
import java.util.Optional;

public interface TradeConfirmationRepository {

    TradeConfirmation save(TradeConfirmation confirmation);

    Optional<TradeConfirmation> findById(Long id);

    Optional<TradeConfirmation> findByConfirmId(String confirmId);

    Optional<TradeConfirmation> findByTradeNo(String tradeNo);

    List<TradeConfirmation> findByMatchStatus(String matchStatus);

    List<TradeConfirmation> findAll();
}
