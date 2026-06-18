package com.forex.margin.domain.model;

import com.forex.margin.domain.model.aggregate.MarginAccount;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MarginAccount domain aggregate.
 * 保证金账户聚合根单元测试。
 */
class MarginAccountTest {

    @Test
    @DisplayName("Create margin account sets initial state")
    void testCreate() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "INITIAL",
                "USD", new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        assertEquals("USD", account.getMarginCurrency());
        assertEquals("PENDING", account.getStatus());
    }

    @Test
    @DisplayName("Deposit increases deposited amount")
    void testDeposit() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "INITIAL",
                "USD", new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        account.deposit(new BigDecimal("30000.00"));
        assertEquals(new BigDecimal("30000.00"), account.getDepositedAmount());
    }

    @Test
    @DisplayName("Release all deposited amount leaves CALLED status due to shortfall")
    void testRelease_All_LeavesShortfall() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "INITIAL",
                "USD", new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        account.deposit(new BigDecimal("50000.00"));
        account.release(new BigDecimal("50000.00"), "POSITION_CLOSED");
        assertEquals(BigDecimal.ZERO.compareTo(account.getDepositedAmount()), 0);
        assertEquals("CALLED", account.getStatus());
    }

    @Test
    @DisplayName("Release excess deposit sets status to RELEASED")
    void testRelease_ExcessDeposit() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "INITIAL",
                "USD", new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        account.deposit(new BigDecimal("60000.00"));
        account.release(new BigDecimal("10000.00"), "OVERPAID");
        assertEquals(new BigDecimal("50000.00"), account.getDepositedAmount());
        assertEquals("RELEASED", account.getStatus());
    }

    @Test
    @DisplayName("Release partial amount sets status to CALLED due to shortfall")
    void testRelease_Partial() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "INITIAL",
                "USD", new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        account.deposit(new BigDecimal("50000.00"));
        account.release(new BigDecimal("20000.00"), "POSITION_CLOSED");
        assertEquals(new BigDecimal("30000.00"), account.getDepositedAmount());
        assertEquals("CALLED", account.getStatus());
    }

    @Test
    @DisplayName("Calculate margin rate by tenor: ≤12 months = 7%")
    void testCalculateMarginRate_Short() {
        BigDecimal rate = MarginAccount.calculateMarginRate(6);
        assertEquals(new BigDecimal("0.0700"), rate.setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Calculate margin rate: 13-36 months = 12%")
    void testCalculateMarginRate_Medium() {
        BigDecimal rate = MarginAccount.calculateMarginRate(24);
        assertEquals(new BigDecimal("0.1200"), rate.setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Calculate margin rate: >36 months = 15%")
    void testCalculateMarginRate_Long() {
        BigDecimal rate = MarginAccount.calculateMarginRate(48);
        assertEquals(new BigDecimal("0.1500"), rate.setScale(4, RoundingMode.HALF_UP));
    }
}
