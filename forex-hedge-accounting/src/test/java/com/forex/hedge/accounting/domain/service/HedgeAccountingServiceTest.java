package com.forex.hedge.accounting.domain.service;

import com.forex.hedge.accounting.domain.model.aggregate.HedgeRelationship;
import com.forex.hedge.accounting.domain.model.entity.HedgeEffectivenessTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HedgeAccountingServiceTest {

    private final HedgeAccountingService service = new HedgeAccountingService();

    private HedgeRelationship createRelation() {
        return HedgeRelationship.create(1L, "HR2026060001", 1001L,
                "FAIR_VALUE", "Loan Receivable USD 1M", "FX Forward USD/CNY 1M",
                new BigDecimal("1000000.00"), "USD",
                new BigDecimal("1000000.00"), "IFRS9");
    }

    @Test
    @DisplayName("Prospective test returns a ratio between 0.80 and 1.25")
    void testProspectiveTest() {
        HedgeRelationship relation = createRelation();
        BigDecimal ratio = service.performProspectiveTest(relation);
        assertNotNull(ratio);
        assertTrue(ratio.compareTo(new BigDecimal("0.80")) >= 0,
                () -> "ratio must be >= 0.80 but was " + ratio);
        assertTrue(ratio.compareTo(new BigDecimal("1.25")) <= 0,
                () -> "ratio must be <= 1.25 but was " + ratio);
    }

    @Test
    @DisplayName("Retrospective test returns PASS when ratio is between 0.80 and 1.25")
    void testRetrospectiveTest_Pass() {
        HedgeRelationship relation = createRelation();
        HedgeEffectivenessTest test = service.performRetrospectiveTest(relation);
        assertNotNull(test);
        assertEquals("RETROSPECTIVE", test.getTestType());
        assertEquals("DOLLAR_OFFSET", test.getTestMethod());
        BigDecimal ratio = test.getTestResult();
        boolean expectedPass = ratio.compareTo(new BigDecimal("0.80")) >= 0
                && ratio.compareTo(new BigDecimal("1.25")) <= 0;
        assertEquals(expectedPass ? "PASS" : "FAIL", test.getResultStatus());
    }

    @Test
    @DisplayName("Generate hedge entries returns 2 entries (DEBIT + CREDIT)")
    void testGenerateHedgeEntries() {
        HedgeRelationship relation = createRelation();
        BigDecimal fvChange = new BigDecimal("50000.00");
        List<Map<String, Object>> entries = service.generateHedgeEntries(relation, fvChange);
        assertNotNull(entries);
        assertEquals(2, entries.size());
        assertEquals("FAIR_VALUE", entries.get(0).get("type"));
        assertEquals("DEBIT", entries.get(0).get("direction"));
        assertEquals("CREDIT", entries.get(1).get("direction"));
    }
}
