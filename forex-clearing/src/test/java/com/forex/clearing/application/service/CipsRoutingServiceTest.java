package com.forex.clearing.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CipsRoutingServiceTest {

    private final CipsRoutingService service = new CipsRoutingService();

    @Test
    @DisplayName("Resolve by BIC returns CIPS participant ID")
    void testResolveByBic_Known() {
        assertEquals("CIPS00001", service.resolveByBic("BKCHCNBJ"));
        assertEquals("CIPS00002", service.resolveByBic("ICBKCNBJ"));
        assertEquals("CIPS00003", service.resolveByBic("ABOCCNBJ"));
    }

    @Test
    @DisplayName("Resolve by unknown BIC returns null")
    void testResolveByBic_Unknown() {
        assertNull(service.resolveByBic("CHASUS33"));
        assertNull(service.resolveByBic(null));
    }

    @Test
    @DisplayName("Resolve by bank name returns CIPS participant ID")
    void testResolveByBankName() {
        assertEquals("CIPS00001", service.resolveByBankName("Bank of China"));
        assertEquals("CIPS00004", service.resolveByBankName("China Construction Bank"));
    }

    @Test
    @DisplayName("Is CIPS participant returns true for known BIC")
    void testIsCipsParticipant() {
        assertTrue(service.isCipsParticipant("BKCHCNBJ"));
        assertFalse(service.isCipsParticipant("CHASUS33"));
    }

    @Test
    @DisplayName("Get participant count returns initialized count")
    void testGetParticipantCount() {
        assertEquals(8, service.getParticipantCount());
    }
}
