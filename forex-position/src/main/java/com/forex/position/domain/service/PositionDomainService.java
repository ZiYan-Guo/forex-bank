package com.forex.position.domain.service;

import com.forex.position.domain.event.PositionLimitBreachEvent;
import com.forex.position.domain.model.aggregate.Position;
import com.forex.position.domain.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionDomainService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String POSITION_NO_PREFIX = "POS";

    private final PositionRepository positionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Position createPosition(String currencyPair, String positionType,
                                    String positionCurrency, BigDecimal positionLimit,
                                    LocalDate positionDate, Long traderId, String branchCode) {
        Position position = Position.create(currencyPair, positionType, positionCurrency,
                positionLimit, positionDate, traderId, branchCode);
        String positionNo = generatePositionNo(currencyPair);
        position.assignPositionNo(positionNo);
        log.info("Created position, positionNo: {}, currencyPair: {}", positionNo, currencyPair);
        return position;
    }

    public Position aggregatePositions(String currencyPair, LocalDate date) {
        log.info("Aggregating positions for currencyPair: {}, date: {}", currencyPair, date);
        return Position.create(currencyPair, "AGGREGATED", null, BigDecimal.ZERO, date, null, null);
    }

    public void checkPositionLimit(Position pos) {
        pos.checkLimit();
        positionRepository.save(pos);
        if ("HIGH".equals(pos.getRiskLevel())) {
            eventPublisher.publishEvent(new PositionLimitBreachEvent(
                    pos.getId(), pos.getPositionLimit(), pos.getLimitUsagePct()));
            log.warn("Position limit breached, positionId: {}, limit: {}, usage: {}",
                    pos.getId(), pos.getPositionLimit(), pos.getLimitUsagePct());
        }
    }

    public String getHedgingAdvice(Position pos) {
        if (pos.getNetPosition() == null || pos.getNetPosition().compareTo(BigDecimal.ZERO) == 0) {
            log.info("No hedging action needed for position: {}", pos.getPositionNo());
            return "NONE";
        }
        if (pos.getNetPosition().compareTo(BigDecimal.ZERO) > 0) {
            log.info("Hedging advice for position {}: SELL to offset long position", pos.getPositionNo());
            return "SELL";
        } else {
            log.info("Hedging advice for position {}: BUY to offset short position", pos.getPositionNo());
            return "BUY";
        }
    }

    private String generatePositionNo(String currencyPair) {
        String datePart = LocalDate.now().format(DATE_FORMAT);
        String suffix = String.format("%06d", (long) (Math.random() * 1_000_000));
        return POSITION_NO_PREFIX + datePart + suffix;
    }
}
