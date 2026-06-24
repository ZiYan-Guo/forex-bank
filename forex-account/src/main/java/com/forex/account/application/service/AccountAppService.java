package com.forex.account.application.service;

import com.forex.account.application.command.AccountOperationCmd;
import com.forex.account.application.command.OpenAccountCmd;
import com.forex.account.application.query.AccountDetailDTO;
import com.forex.account.application.query.AccountQuery;
import com.forex.account.application.query.TransactionDTO;
import com.forex.account.domain.event.AccountOpenedEvent;
import com.forex.account.domain.event.BalanceChangedEvent;
import com.forex.account.domain.model.aggregate.ForexAccount;
import com.forex.account.domain.model.entity.AccountTransaction;
import com.forex.account.domain.repository.AccountRepository;
import com.forex.account.domain.repository.TransactionRepository;
import com.forex.account.domain.service.AccountDomainService;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountAppService {

    private final AccountDomainService accountDomainService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ForexAccount openAccount(OpenAccountCmd cmd) {
        ForexAccount account = accountDomainService.openAccount(
                cmd.getCustomerId(), cmd.getAccountType(), cmd.getCurrency(),
                cmd.getAccountName(), cmd.getOpenBranch());
        ForexAccount saved = accountRepository.save(account);

        eventPublisher.publishEvent(new AccountOpenedEvent(
                saved.getId(), saved.getAccountNo(), saved.getCustomerId(), saved.getCurrency()));

        return saved;
    }

    public void closeAccount(Long accountId) {
        ForexAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND));
        accountDomainService.closeAccount(account);
        accountRepository.save(account);
    }

    public ForexAccount getAccountDetail(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND));
    }

    public ForexAccount getAccountByNo(String accountNo) {
        return accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND));
    }

    public List<ForexAccount> getCustomerAccounts(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public PageResp<AccountTransaction> getTransactions(Long accountId, PageReq pageReq) {
        List<AccountTransaction> list = transactionRepository.findByAccountId(accountId, pageReq);
        long total = transactionRepository.countByAccountId(accountId);
        return PageResp.of(total, list, pageReq.getPageNum(), pageReq.getPageSize());
    }

    @RedisLock(key = "#accountId")
    public void deposit(Long accountId, BigDecimal amount, String relatedBizNo, String summary) {
        ForexAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND));

        AccountTransaction tx = accountDomainService.recordTransaction(
                account, "DEPOSIT", amount, relatedBizNo, "DEPOSIT", summary);
        accountRepository.save(account);
        transactionRepository.save(tx);

        eventPublisher.publishEvent(new BalanceChangedEvent(
                account.getId(), account.getAccountNo(), amount, "DEPOSIT", account.getBalance()));
    }

    @RedisLock(key = "#accountId")
    public void withdraw(Long accountId, BigDecimal amount, String relatedBizNo, String summary) {
        ForexAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND));

        AccountTransaction tx = accountDomainService.recordTransaction(
                account, "WITHDRAW", amount, relatedBizNo, "WITHDRAW", summary);
        accountRepository.save(account);
        transactionRepository.save(tx);

        eventPublisher.publishEvent(new BalanceChangedEvent(
                account.getId(), account.getAccountNo(), amount, "WITHDRAW", account.getBalance()));
    }

    @RedisLock(key = "#accountId")
    public void freeze(Long accountId, BigDecimal amount) {
        ForexAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND));
        account.freeze(amount);
        accountRepository.save(account);

        eventPublisher.publishEvent(new BalanceChangedEvent(
                account.getId(), account.getAccountNo(), amount, "FREEZE", account.getBalance()));
    }

    @RedisLock(key = "#accountId")
    public void unfreeze(Long accountId, BigDecimal amount) {
        ForexAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND));
        account.unfreeze(amount);
        accountRepository.save(account);

        eventPublisher.publishEvent(new BalanceChangedEvent(
                account.getId(), account.getAccountNo(), amount, "UNFREEZE", account.getBalance()));
    }
}
