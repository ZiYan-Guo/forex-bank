package com.forex.account.domain.service;

import com.forex.account.domain.model.aggregate.ForexAccount;
import com.forex.account.domain.model.entity.AccountTransaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDomainService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ACCOUNT_NO_PREFIX = "AC";

    public ForexAccount openAccount(Long customerId, String accountType, String currency,
                                     String accountName, String openBranch) {
        ForexAccount account = ForexAccount.create(customerId, accountType, currency, accountName, openBranch);
        String accountNo = generateAccountNo(currency);
        account.assignAccountNo(accountNo);
        log.info("开立外汇账户, accountNo: {}, customerId: {}, currency: {}", accountNo, customerId, currency);
        return account;
    }

    public void closeAccount(ForexAccount account) {
        account.close();
        log.info("关闭外汇账户, accountNo: {}", account.getAccountNo());
    }

    public void freezeAccount(ForexAccount account) {
        account.setFrozen();
        log.info("冻结外汇账户, accountNo: {}", account.getAccountNo());
    }

    public void unfreezeAccount(ForexAccount account) {
        account.setNormal();
        log.info("解冻外汇账户, accountNo: {}", account.getAccountNo());
    }

    public AccountTransaction recordTransaction(ForexAccount account, String txType,
                                                 BigDecimal amount, String relatedBizNo,
                                                 String relatedBizType, String summary) {
        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter;
        if ("DEPOSIT".equals(txType)) {
            account.deposit(amount);
            balanceAfter = account.getBalance();
        } else if ("WITHDRAW".equals(txType)) {
            account.withdraw(amount);
            balanceAfter = account.getBalance();
        } else {
            throw new IllegalArgumentException("不支持的交易类型: " + txType);
        }
        return AccountTransaction.record(
                account.getId(),
                account.getAccountNo(),
                txType,
                amount,
                account.getCurrency(),
                balanceBefore,
                balanceAfter,
                relatedBizNo,
                relatedBizType,
                summary
        );
    }

    private String generateAccountNo(String currency) {
        String datePart = LocalDate.now().format(DATE_FORMAT);
        String suffix = String.format("%08d", (long) (Math.random() * 100_000_000));
        return ACCOUNT_NO_PREFIX + datePart + currency + suffix;
    }
}
