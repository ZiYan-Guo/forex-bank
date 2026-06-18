package com.forex.position.domain.service;

import com.forex.position.domain.event.PositionLimitBreachEvent;
import com.forex.position.domain.model.aggregate.Position;
import com.forex.position.domain.model.entity.PositionLimitConfig;
import com.forex.position.domain.repository.PositionLimitConfigRepository;
import com.forex.position.domain.repository.PositionRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionDomainServiceTest {

    @Mock private PositionRepository positionRepository;
    @Mock private PositionLimitConfigRepository limitConfigRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PositionDomainService positionDomainService;

    @Captor
    private ArgumentCaptor<PositionLimitBreachEvent> breachCaptor;

    @Test
    @DisplayName("Create position returns position with generated number")
    void testCreatePosition() {
        PositionLimitConfig config = new PositionLimitConfig(1L, "USD", "SPOT_LIMIT",
                new BigDecimal("1000000"), new BigDecimal("80"), 1);
        when(limitConfigRepository.findByCurrency("USD")).thenReturn(List.of(config));

        Position result = positionDomainService.createPosition("USD/CNY", "SPOT", "USD", 1001L, "SH");

        assertNotNull(result);
        assertTrue(result.getPositionNo().startsWith("POS"));
    }

    @Test
    @DisplayName("Aggregate positions sums longs and shorts")
    void testAggregatePositions() {
        Position p1 = createPosition("USD/CNY", new BigDecimal("100000"), new BigDecimal("50000"));
        Position p2 = createPosition("USD/CNY", new BigDecimal("50000"), new BigDecimal("20000"));
        when(positionRepository.findByCurrencyPairAndDate("USD/CNY", LocalDate.now()))
                .thenReturn(List.of(p1, p2));
        PositionLimitConfig config = new PositionLimitConfig(1L, "USD", "SPOT_LIMIT",
                new BigDecimal("5000000"), new BigDecimal("80"), 1);
        when(limitConfigRepository.findByCurrency("USD")).thenReturn(List.of(config));

        Position aggregated = positionDomainService.aggregatePositions("USD/CNY", LocalDate.now());

        assertNotNull(aggregated);
        assertEquals(new BigDecimal("150000"), aggregated.getLongAmount());
        assertEquals(new BigDecimal("70000"), aggregated.getShortAmount());
    }

    @Test
    @DisplayName("Check position limit publishes breach event when exceeded")
    void testCheckPositionLimit_Breach() {
        // Position with limit=100000 and long=500000 → usage=500% → BREACH
        Position position = Position.create("USD/CNY", "SPOT", "USD",
                new BigDecimal("100000"), LocalDate.now(), 1001L, "SH");
        position.assignPositionNo("POS20260601000001");
        position.addLong(new BigDecimal("500000"));
        PositionLimitConfig config = new PositionLimitConfig(1L, "USD", "SPOT_LIMIT",
                new BigDecimal("100000"), new BigDecimal("80"), 1);
        when(limitConfigRepository.findByCurrency("USD")).thenReturn(List.of(config));

        positionDomainService.checkPositionLimit(position);

        verify(positionRepository).save(position);
        verify(eventPublisher).publishEvent(any(PositionLimitBreachEvent.class));
        assertEquals("BREACH", position.getRiskLevel());
    }

    @Test
    @DisplayName("Check position limit logs warning when approaching limit")
    void testCheckPositionLimit_Warning() {
        // Position with limit=1000000 and long=500000 → usage=50%, warningPct=80 → NORMAL (50<80)
        // Use warningPct=30 so that 50>=30 → WARNING
        Position position = Position.create("USD/CNY", "SPOT", "USD",
                new BigDecimal("1000000"), LocalDate.now(), 1001L, "SH");
        position.assignPositionNo("POS20260601000002");
        position.addLong(new BigDecimal("500000"));
        PositionLimitConfig config = new PositionLimitConfig(1L, "USD", "SPOT_LIMIT",
                new BigDecimal("1000000"), new BigDecimal("30"), 1);
        when(limitConfigRepository.findByCurrency("USD")).thenReturn(List.of(config));

        positionDomainService.checkPositionLimit(position);

        verify(positionRepository).save(position);
        assertEquals("WARNING", position.getRiskLevel());
    }

    @Test
    @DisplayName("Get hedging advice returns SELL for long position")
    void testGetHedgingAdvice_Long() {
        Position position = createPosition("USD/CNY", new BigDecimal("100000"), BigDecimal.ZERO);
        assertEquals("SELL", positionDomainService.getHedgingAdvice(position));
    }

    @Test
    @DisplayName("Get hedging advice returns BUY for short position")
    void testGetHedgingAdvice_Short() {
        Position position = createPosition("USD/CNY", BigDecimal.ZERO, new BigDecimal("50000"));
        assertEquals("BUY", positionDomainService.getHedgingAdvice(position));
    }

    @Test
    @DisplayName("Get hedging advice returns NONE for flat position")
    void testGetHedgingAdvice_Flat() {
        Position position = createPosition("USD/CNY", BigDecimal.ZERO, BigDecimal.ZERO);
        assertEquals("NONE", positionDomainService.getHedgingAdvice(position));
    }

    private Position createPosition(String pair, BigDecimal longAmt, BigDecimal shortAmt) {
        Position pos = Position.create(pair, "SPOT", pair.split("/")[0],
                new BigDecimal("1000000"), LocalDate.now(), 1001L, "SH");
        pos.assignPositionNo("POS20260601000001");
        if (longAmt.compareTo(BigDecimal.ZERO) > 0) pos.addLong(longAmt);
        if (shortAmt.compareTo(BigDecimal.ZERO) > 0) pos.addShort(shortAmt);
        return pos;
    }
}
