package com.forex.position.domain.service;

import com.forex.position.domain.event.PositionLimitBreachEvent;
import com.forex.position.domain.model.aggregate.Position;
import com.forex.position.domain.model.entity.PositionLimitConfig;
import com.forex.position.domain.repository.PositionLimitConfigRepository;
import com.forex.position.domain.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Position domain service. Manages position lifecycle, aggregation and limit checking.
 * 敞口领域服务。管理头寸生命周期、汇总和限额检查。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PositionDomainService {

    private final PositionRepository positionRepository;
    private final PositionLimitConfigRepository limitConfigRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Position createPosition(String currencyPair, String positionType,
                                    String positionCurrency, Long traderId, String branchCode) {
        PositionLimitConfig config = getLimitConfigForCurrency(positionCurrency);
        BigDecimal limit = config != null ? config.getLimitAmount() : BigDecimal.ZERO;
        Position position = Position.create(currencyPair, positionType, positionCurrency,
                limit, LocalDate.now(), traderId, branchCode);
        position.assignPositionNo(generatePositionNo(currencyPair));
        return position;
    }

    /**
     * Aggregate all positions for a currency pair on a given date.
     * 汇总指定日期和货币对的所有敞口。
     */
    public Position aggregatePositions(String currencyPair, LocalDate date) {
        List<Position> positions = positionRepository.findByCurrencyPairAndDate(currencyPair, date);
        Position aggregated = Position.create(currencyPair, "AGGREGATE", 
                extractCurrency(currencyPair), BigDecimal.ZERO, date, null, null);
        aggregated.assignPositionNo("POS_AGG_" + date + "_" + currencyPair.replace("/", "_"));
        
        BigDecimal totalLong = positions.stream()
                .map(Position::getLongAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalShort = positions.stream()
                .map(Position::getShortAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalLong.compareTo(BigDecimal.ZERO) > 0) aggregated.addLong(totalLong);
        if (totalShort.compareTo(BigDecimal.ZERO) > 0) aggregated.addShort(totalShort);
        
        PositionLimitConfig config = getLimitConfigForCurrency(extractCurrency(currencyPair));
        if (config != null) {
            aggregated.assignPositionNo(aggregated.getPositionNo());
            aggregated.checkLimit(config.getWarningPct());
        }
        
        log.info("Aggregated positions for {} on {}: long={}, short={}, net={}", 
                currencyPair, date, totalLong, totalShort, aggregated.getNetPosition());
        return aggregated;
    }

    /**
     * Check position against limit configuration and publish breach event.
     * 检查敞口限额并发布超限事件。
     */
    public void checkPositionLimit(Position position) {
        PositionLimitConfig config = getLimitConfigForCurrency(position.getPositionCurrency());
        if (config != null) {
            position.checkLimit(config.getWarningPct());
        } else {
            position.checkLimit();
        }
        positionRepository.save(position);
        
        if (Position.RISK_BREACH.equals(position.getRiskLevel())) {
            eventPublisher.publishEvent(new PositionLimitBreachEvent(
                    position.getId(), position.getPositionLimit(), position.getLimitUsagePct()));
            log.warn("Position limit breach: positionNo={}, currency={}, usage={}%",
                    position.getPositionNo(), position.getPositionCurrency(), position.getLimitUsagePct());
        } else if (Position.RISK_WARNING.equals(position.getRiskLevel())) {
            log.warn("Position limit warning: positionNo={}, currency={}, usage={}%",
                    position.getPositionNo(), position.getPositionCurrency(), position.getLimitUsagePct());
        }
    }

    public String getHedgingAdvice(Position pos) {
        if (pos.getNetPosition() == null || pos.getNetPosition().compareTo(BigDecimal.ZERO) == 0) {
            return "NONE";
        }
        return pos.getNetPosition().compareTo(BigDecimal.ZERO) > 0 ? "SELL" : "BUY";
    }

    private PositionLimitConfig getLimitConfigForCurrency(String currency) {
        if (currency == null) return null;
        List<PositionLimitConfig> configs = limitConfigRepository.findByCurrency(currency);
        return configs.isEmpty() ? null : configs.get(0);
    }

    private String generatePositionNo(String currencyPair) {
        return "POS" + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%06d", (int)(Math.random() * 1000000));
    }

    private String extractCurrency(String currencyPair) {
        if (currencyPair == null || !currencyPair.contains("/")) return "CNY";
        return currencyPair.split("/")[0];
    }
}
