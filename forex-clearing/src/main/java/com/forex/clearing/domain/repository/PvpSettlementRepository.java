package com.forex.clearing.domain.repository;

import com.forex.clearing.domain.model.aggregate.PvpSettlementPair;

import java.util.Optional;

public interface PvpSettlementRepository {

    PvpSettlementPair save(PvpSettlementPair pair);

    Optional<PvpSettlementPair> findById(Long id);

    Optional<PvpSettlementPair> findByPairId(String pairId);
}
