package com.forex.settlement.domain.model.entity;

import com.forex.common.base.exception.BusinessException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankGuaranteeTest {

    @Test
    @DisplayName("Issue moves draft guarantee to issued")
    void issueMovesDraftGuaranteeToIssued() {
        BankGuarantee guarantee = newDraftGuarantee();
        LocalDate issueDate = LocalDate.of(2026, 8, 5);

        guarantee.issue(issueDate);

        assertEquals(BankGuarantee.STATUS_ISSUED, guarantee.getGuaranteeStatus());
        assertEquals(issueDate, guarantee.getIssueDate());
    }

    @Test
    @DisplayName("Issue rejects non-draft guarantee")
    void issueRejectsNonDraftGuarantee() {
        BankGuarantee guarantee = newDraftGuarantee();
        guarantee.issue(LocalDate.of(2026, 8, 5));

        assertThrows(BusinessException.class, () -> guarantee.issue(LocalDate.of(2026, 8, 6)));
    }

    private BankGuarantee newDraftGuarantee() {
        return new BankGuarantee(
                1L,
                "BG202608050001",
                1001L,
                "PERFORMANCE",
                new BigDecimal("100000.00"),
                "USD",
                "Global Trading Ltd.",
                null,
                LocalDate.of(2026, 8, 5),
                LocalDate.of(2027, 8, 5),
                null,
                null,
                "DIRECT",
                BankGuarantee.STATUS_DRAFT,
                BigDecimal.ZERO,
                new BigDecimal("0.001000"),
                9001L,
                null,
                null);
    }
}
