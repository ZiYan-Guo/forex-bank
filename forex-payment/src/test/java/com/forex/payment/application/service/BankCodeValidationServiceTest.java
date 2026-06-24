package com.forex.payment.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankCodeValidationServiceTest {

    private final BankCodeValidationService service = new BankCodeValidationService();

    @Test
    @DisplayName("Valid SWIFT code returns true")
    void testValidateSwiftCode_Valid() {
        assertTrue(service.validateSwiftCode("CHASUS33"));
        assertTrue(service.validateSwiftCode("BKCHCNBJXXX"));
        assertTrue(service.validateSwiftCode("ICBKCNBJ"));
    }

    @Test
    @DisplayName("Invalid SWIFT code returns false")
    void testValidateSwiftCode_Invalid() {
        assertFalse(service.validateSwiftCode("INVALID"));
        assertFalse(service.validateSwiftCode("12345678"));
        assertFalse(service.validateSwiftCode(""));
        assertFalse(service.validateSwiftCode(null));
    }

    @Test
    @DisplayName("Auto complete BIC returns full code for known bank")
    void testAutoCompleteBic_Known() {
        assertEquals("BKCHCNBJXXX", service.autoCompleteBic("BKCH", "中国银行"));
        assertEquals("ICBKCNBJXXX", service.autoCompleteBic("ICBK", "工商银行"));
        assertEquals("CITIUS33XXX", service.autoCompleteBic("CITI", "Citibank"));
    }

    @Test
    @DisplayName("Auto complete BIC returns input when 8+ chars")
    void testAutoCompleteBic_LongEnough() {
        assertEquals("CHASUS33XXX", service.autoCompleteBic("CHASUS33XXX", "Chase"));
    }

    @Test
    @DisplayName("Calculate SWIFT fee with OUR charge bearer returns 52.5")
    void testCalculateFee_SWIFT_OUR() {
        BigDecimal fee = service.calculateFee("SWIFT", "OUR", new BigDecimal("10000"));
        assertEquals(0, fee.compareTo(new BigDecimal("52.5")));
    }

    @Test
    @DisplayName("Calculate CIPS fee returns 25")
    void testCalculateFee_CIPS_SHA() {
        BigDecimal fee = service.calculateFee("CIPS", "SHA", new BigDecimal("10000"));
        assertEquals(0, fee.compareTo(new BigDecimal("25")));
    }

    @Test
    @DisplayName("Calculate fee with BEN charge bearer returns zero")
    void testCalculateFee_BEN() {
        BigDecimal fee = service.calculateFee("SWIFT", "BEN", new BigDecimal("10000"));
        assertEquals(BigDecimal.ZERO, fee);
    }
}
