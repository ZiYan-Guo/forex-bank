package com.forex.cashpool.domain.service;

import com.forex.cashpool.domain.model.entity.PoolMember;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NettingSettlementService.
 * 轧差结算服务单元测试。
 */
class NettingSettlementServiceTest {

    private final NettingSettlementService service = new NettingSettlementService();

    private List<PoolMember> createMembers() {
        return List.of(
                new PoolMember(1L, "POOL001", 101L, "DOMESTIC", "USD", "REALTIME",
                        new BigDecimal("500000.00"), LocalDate.now()),
                new PoolMember(2L, "POOL001", 102L, "DOMESTIC", "USD", "REALTIME",
                        new BigDecimal("300000.00"), LocalDate.now()),
                new PoolMember(3L, "POOL001", 103L, "OVERSEAS", "USD", "DAILY",
                        new BigDecimal("200000.00"), LocalDate.now())
        );
    }

    @Test
    @DisplayName("Calculate net position for members returns position map")
    void testCalculateNetPosition() {
        Map<Long, BigDecimal> positions = service.calculateNetPosition(createMembers(), LocalDate.now());
        assertNotNull(positions);
        assertEquals(3, positions.size());
        assertEquals(new BigDecimal("500000.00"), positions.get(101L));
        assertEquals(new BigDecimal("300000.00"), positions.get(102L));
    }

    @Test
    @DisplayName("Calculate net position with empty members returns empty map")
    void testCalculateNetPosition_Empty() {
        Map<Long, BigDecimal> positions = service.calculateNetPosition(List.of(), LocalDate.now());
        assertTrue(positions.isEmpty());
    }

    @Test
    @DisplayName("Calculate net position with null members returns empty map")
    void testCalculateNetPosition_Null() {
        Map<Long, BigDecimal> positions = service.calculateNetPosition(null, LocalDate.now());
        assertTrue(positions.isEmpty());
    }

    @Test
    @DisplayName("Generate netting instruction contains pool info and positions")
    void testGenerateNettingInstruction() {
        Map<Long, BigDecimal> positions = Map.of(
                101L, new BigDecimal("500000.00"),
                102L, new BigDecimal("-200000.00")
        );
        String instruction = service.generateNettingInstruction(positions, "POOL001", LocalDate.of(2026, 6, 1));
        assertTrue(instruction.contains("POOL001"));
        assertTrue(instruction.contains("2026-06-01"));
        assertTrue(instruction.contains("应收"));
        assertTrue(instruction.contains("应付"));
    }

    @Test
    @DisplayName("Generate netting instruction with empty positions")
    void testGenerateNettingInstruction_Empty() {
        String instruction = service.generateNettingInstruction(Map.of(), "POOL002", LocalDate.now());
        assertTrue(instruction.contains("POOL002"));
    }
}
