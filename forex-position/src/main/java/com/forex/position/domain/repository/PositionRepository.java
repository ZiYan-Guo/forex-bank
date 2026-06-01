package com.forex.position.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.position.domain.model.aggregate.Position;
import com.forex.position.domain.model.query.PositionQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PositionRepository {

    Position save(Position position);

    Optional<Position> findById(Long id);

    Optional<Position> findByPositionNo(String positionNo);

    PageResp<Position> pageQuery(PositionQuery query);

    List<Position> findByCurrencyPairAndDate(String currencyPair, LocalDate date);
}
