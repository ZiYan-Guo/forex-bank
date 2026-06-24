package com.forex.ai.domain.service;

import com.forex.ai.domain.model.aggregate.DocumentAudit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentAuditServiceTest {

    private final DocumentAuditService service = new DocumentAuditService();

    @Test
    @DisplayName("Compare documents marks consistent when all match")
    void testCompareDocuments_Consistent() {
        DocumentAudit result = service.compareDocuments(
                "INVOICE_OCR_DATA", "CONTRACT_OCR_DATA", "CUSTOMS_OCR_DATA");

        assertNotNull(result);
        assertTrue(result.getIsConsistent());
        assertEquals(0, result.getConfidenceScore().compareTo(new java.math.BigDecimal("0.95")));
    }

    @Test
    @DisplayName("Compare documents with null input marks discrepancy")
    void testCompareDocuments_NullInput() {
        DocumentAudit result = service.compareDocuments(null, "CONTRACT", "CUSTOMS");

        assertFalse(result.getIsConsistent());
        assertTrue(result.getDiscrepancyDetail().contains("未上传"));
    }

    @Test
    @DisplayName("Extract invoice fields returns JSON")
    void testExtractKeyFields_Invoice() {
        String result = service.extractKeyFields("SOME_OCR_TEXT", DocumentAudit.INVOICE);
        assertTrue(result.contains("amount"));
        assertTrue(result.contains("currency"));
    }

    @Test
    @DisplayName("Extract key fields with null returns empty JSON")
    void testExtractKeyFields_NullText() {
        assertEquals("{}", service.extractKeyFields(null, DocumentAudit.INVOICE));
    }
}
