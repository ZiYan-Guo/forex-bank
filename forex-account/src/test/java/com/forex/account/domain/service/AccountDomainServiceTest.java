package com.forex.account.domain.service;

import com.forex.account.domain.model.aggregate.ForexAccount;
import com.forex.account.domain.model.entity.AccountTransaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountDomainServiceTest {

    private final AccountDomainService service = new AccountDomainService();

    @Test
    @DisplayName("Open account generates account number and returns account")
    void testOpenAccount() {
        ForexAccount account = service.openAccount(1001L, "CORPORATE", "USD",
                "TEST ACCOUNT", "SH001");

        assertNotNull(account);
        assertTrue(account.getAccountNo().startsWith("AC"));
        assertEquals("USD", account.getCurrency());
        assertEquals("NORMAL", account.getAccountStatus());
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Close account sets status to CLOSED")
    void testCloseAccount() {
        ForexAccount account = service.openAccount(1001L, "CORPORATE", "CNY",
                "TEST", "BJ001");
        service.closeAccount(account);
        assertEquals("CLOSED", account.getAccountStatus());
    }

    @Test
    @DisplayName("Freeze account sets status to FROZEN")
    void testFreezeAccount() {
        ForexAccount account = service.openAccount(1001L, "CORPORATE", "EUR",
                "TEST", "SH001");
        service.freezeAccount(account);
        assertEquals("FROZEN", account.getAccountStatus());
    }

    @Test
    @DisplayName("Unfreeze account restores NORMAL")
    void testUnfreezeAccount() {
        ForexAccount account = service.openAccount(1001L, "CORPORATE", "GBP",
                "TEST", "SH001");
        service.freezeAccount(account);
        service.unfreezeAccount(account);
        assertEquals("NORMAL", account.getAccountStatus());
    }

    @Test
    @DisplayName("Record deposit transaction updates balance")
    void testRecordTransaction_Deposit() {
        ForexAccount account = service.openAccount(1001L, "CORPORATE", "USD",
                "TEST", "SH001");
        AccountTransaction tx = service.recordTransaction(account, "DEPOSIT",
                new BigDecimal("10000.00"), "TXN001", "PAYMENT", "入金");
        assertNotNull(tx);
        assertTrue(tx.getTransactionNo().startsWith("TXN"));
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("10000.00")));
    }

    @Test
    @DisplayName("Record withdraw transaction reduces balance")
    void testRecordTransaction_Withdraw() {
        ForexAccount account = service.openAccount(1001L, "CORPORATE", "USD",
                "TEST", "SH001");
        service.recordTransaction(account, "DEPOSIT",
                new BigDecimal("5000.00"), "TXN001", "PAYMENT", "入金");
        AccountTransaction tx = service.recordTransaction(account, "WITHDRAW",
                new BigDecimal("3000.00"), "TXN002", "PAYMENT", "出金");
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("2000.00")));
    }
}
