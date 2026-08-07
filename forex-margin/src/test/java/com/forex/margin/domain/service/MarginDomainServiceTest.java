package com.forex.margin.domain.service;

import com.forex.common.base.dto.PageResp;
import com.forex.margin.domain.event.MarginCalledEvent;
import com.forex.margin.domain.model.aggregate.MarginAccount;
import com.forex.margin.domain.model.entity.MarginCall;
import com.forex.margin.domain.model.query.MarginQuery;
import com.forex.margin.domain.repository.MarginAccountRepository;
import com.forex.margin.domain.repository.MarginCallRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for MarginDomainService.
 * 保证金领域服务单元测试。
 */
class MarginDomainServiceTest {

    private final FakeMarginAccountRepository marginAccountRepository = new FakeMarginAccountRepository();
    private final FakeMarginCallRepository marginCallRepository = new FakeMarginCallRepository();
    private final CapturingEventPublisher eventPublisher = new CapturingEventPublisher();
    private final MarginDomainService marginDomainService = new MarginDomainService(
            marginAccountRepository, marginCallRepository, eventPublisher);

    @Test
    @DisplayName("Create margin returns account with margin number")
    void testCreateMargin() {
        MarginAccount result = marginDomainService.createMargin(
                1001L, 2001L, "INITIAL", "USD",
                new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        assertNotNull(result);
        assertTrue(result.getMarginNo().startsWith("MG"));
    }

    @Test
    @DisplayName("Calculate initial margin returns product of notional and rate")
    void testCalculateInitialMargin() {
        BigDecimal result = marginDomainService.calculateInitialMargin(
                new BigDecimal("100000.00"), new BigDecimal("0.10"));
        assertEquals(0, result.compareTo(new BigDecimal("10000.00")));
    }

    @Test
    @DisplayName("Calculate initial margin with null returns zero")
    void testCalculateInitialMargin_Null() {
        assertEquals(BigDecimal.ZERO, marginDomainService.calculateInitialMargin(null, new BigDecimal("0.10")));
    }

    @Test
    @DisplayName("Call margin saves account and publishes event")
    void testCallMargin() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "VARIATION",
                "USD", new BigDecimal("10000.00"), new BigDecimal("0.08"), "CASH");
        account.assignMarginNo("MG2026060100000001");

        MarginAccount result = marginDomainService.callMargin(account, new BigDecimal("5000.00"));

        assertEquals(account, marginAccountRepository.savedAccounts.get(0));
        assertEquals(account, result);
        assertTrue(eventPublisher.events.get(0) instanceof MarginCalledEvent);
        MarginCalledEvent event = (MarginCalledEvent) eventPublisher.events.get(0);
        assertEquals(account.getId(), event.getMarginId());
    }

    @Test
    @DisplayName("Release margin saves account and sets release reason")
    void testReleaseMargin() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "INITIAL",
                "USD", new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        account.deposit(new BigDecimal("50000.00"));

        MarginAccount result = marginDomainService.releaseMargin(account, new BigDecimal("50000.00"), "POSITION_CLOSED");

        assertEquals("POSITION_CLOSED", result.getReleaseReason());
        assertEquals(account, marginAccountRepository.savedAccounts.get(0));
    }

    @Test
    @DisplayName("Deposit with water level check triggers margin call when shortfall exists")
    void testDepositWithWaterLevelCheck() {
        MarginAccount account = MarginAccount.create(1001L, 2001L, "INITIAL",
                "USD", new BigDecimal("50000.00"), new BigDecimal("0.08"), "CASH");
        account.assignMarginNo("MG2026060100000002");

        MarginAccount result = marginDomainService.depositWithWaterLevelCheck(account, new BigDecimal("10000.00"));

        assertEquals(new BigDecimal("10000.00"), result.getDepositedAmount());
        assertEquals(1, marginCallRepository.savedCalls.size());
        assertEquals("PENDING", marginCallRepository.savedCalls.get(0).getResponseStatus());
    }

    private static class FakeMarginAccountRepository implements MarginAccountRepository {
        private final List<MarginAccount> savedAccounts = new ArrayList<>();

        @Override
        public MarginAccount save(MarginAccount marginAccount) {
            savedAccounts.add(marginAccount);
            return marginAccount;
        }

        @Override
        public Optional<MarginAccount> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public Optional<MarginAccount> findByMarginNo(String marginNo) {
            return Optional.empty();
        }

        @Override
        public List<MarginAccount> findByCustomerId(Long customerId) {
            return List.of();
        }

        @Override
        public List<MarginAccount> listForLedgerSummary() {
            return List.of();
        }

        @Override
        public PageResp<MarginAccount> pageQuery(MarginQuery query) {
            return PageResp.of(0, List.of(), query.getPageNum(), query.getPageSize());
        }
    }

    private static class FakeMarginCallRepository implements MarginCallRepository {
        private final List<MarginCall> savedCalls = new ArrayList<>();

        @Override
        public void save(MarginCall marginCall) {
            savedCalls.add(marginCall);
        }

        @Override
        public MarginCall findById(Long id) {
            return null;
        }

        @Override
        public List<MarginCall> findByMarginId(Long marginId) {
            return List.of();
        }
    }

    private static class CapturingEventPublisher implements ApplicationEventPublisher {
        private final List<Object> events = new ArrayList<>();

        @Override
        public void publishEvent(Object event) {
            events.add(event);
        }
    }
}
