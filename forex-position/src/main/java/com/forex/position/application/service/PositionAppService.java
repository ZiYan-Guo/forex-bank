package com.forex.position.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.position.application.command.PositionCmd;
import com.forex.position.domain.model.aggregate.Position;
import com.forex.position.domain.model.query.PositionQuery;
import com.forex.position.domain.repository.PositionRepository;
import com.forex.position.domain.service.PositionDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PositionAppService {

    private final PositionRepository positionRepository;
    private final PositionDomainService positionDomainService;

    public Position createPosition(PositionCmd cmd) {
        Position position = positionDomainService.createPosition(
                cmd.getCurrencyPair(), cmd.getPositionType(), cmd.getPositionCurrency(),
                cmd.getTraderId(), cmd.getBranchCode());
        if (cmd.getLongAmount() != null) {
            position.addLong(cmd.getLongAmount());
        }
        if (cmd.getShortAmount() != null) {
            position.addShort(cmd.getShortAmount());
        }
        return positionRepository.save(position);
    }

    public Position updatePosition(Long id, BigDecimal longAmt, BigDecimal shortAmt) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("头寸不存在"));
        if (longAmt != null) {
            position.addLong(longAmt);
        }
        if (shortAmt != null) {
            position.addShort(shortAmt);
        }
        positionDomainService.checkPositionLimit(position);
        return positionRepository.save(position);
    }

    public Position getPositionDetail(String positionNo) {
        return positionRepository.findByPositionNo(positionNo)
                .orElseThrow(() -> new IllegalArgumentException("头寸不存在"));
    }

    public PageResp<Position> pageQuery(PositionQuery query) {
        return positionRepository.pageQuery(query);
    }

    public Position aggregatePositions(LocalDate date, String ccyPair) {
        Position aggregated = positionDomainService.aggregatePositions(ccyPair, date);
        return positionRepository.save(aggregated);
    }

    public Position checkBreach() {
        return null;
    }
}
