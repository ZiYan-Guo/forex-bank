package com.forex.account.adapter.controller;

import com.forex.account.adapter.dto.AccountOperationReq;
import com.forex.account.adapter.dto.AccountResp;
import com.forex.account.adapter.dto.OpenAccountReq;
import com.forex.account.adapter.dto.TransactionResp;
import com.forex.account.application.command.AccountOperationCmd;
import com.forex.account.application.command.OpenAccountCmd;
import com.forex.account.application.service.AccountAppService;
import com.forex.account.domain.model.aggregate.ForexAccount;
import com.forex.account.domain.model.entity.AccountTransaction;
import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.common.security.annotation.RequirePermission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "账户管理")
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountAppService accountAppService;

    @Operation(summary = "开立账户")
    @PostMapping("/open")
    @RequirePermission("account:open")
    @Idempotent(key = "#req.customerId + '_' + #req.currency + '_open'")
    public R<AccountResp> openAccount(@Valid @RequestBody OpenAccountReq req) {
        OpenAccountCmd cmd = new OpenAccountCmd();
        cmd.setCustomerId(req.getCustomerId());
        cmd.setAccountType(req.getAccountType());
        cmd.setCurrency(req.getCurrency());
        cmd.setAccountName(req.getAccountName());
        cmd.setOpenBranch(req.getOpenBranch());

        ForexAccount account = accountAppService.openAccount(cmd);
        return R.ok("开户成功", toAccountResp(account));
    }

    @Operation(summary = "关闭账户")
    @PostMapping("/close/{id}")
    @RequirePermission("account:close")
    @RedisLock(key = "#id")
    public R<Void> closeAccount(@PathVariable Long id) {
        accountAppService.closeAccount(id);
        return R.okMsg("账户已关闭");
    }

    @Operation(summary = "根据ID查询账户")
    @GetMapping("/{id}")
    public R<AccountResp> getById(@PathVariable Long id) {
        ForexAccount account = accountAppService.getAccountDetail(id);
        return R.ok(toAccountResp(account));
    }

    @Operation(summary = "根据账号查询账户")
    @GetMapping("/no/{accountNo}")
    public R<AccountResp> getByAccountNo(@PathVariable String accountNo) {
        ForexAccount account = accountAppService.getAccountByNo(accountNo);
        return R.ok(toAccountResp(account));
    }

    @Operation(summary = "查询客户下所有账户")
    @GetMapping("/customer/{customerId}")
    public R<List<AccountResp>> getCustomerAccounts(@PathVariable Long customerId) {
        List<ForexAccount> accounts = accountAppService.getCustomerAccounts(customerId);
        List<AccountResp> respList = accounts.stream()
                .map(this::toAccountResp)
                .toList();
        return R.ok(respList);
    }

    @Operation(summary = "存款")
    @PostMapping("/deposit")
    @RequirePermission("account:deposit")
    @RedisLock(key = "#req.accountId")
    @Idempotent(key = "#req.relatedBizNo")
    public R<AccountResp> deposit(@Valid @RequestBody AccountOperationReq req) {
        accountAppService.deposit(req.getAccountId(), req.getAmount(),
                req.getRelatedBizNo(), req.getSummary());
        ForexAccount account = accountAppService.getAccountDetail(req.getAccountId());
        return R.ok("存款成功", toAccountResp(account));
    }

    @Operation(summary = "取款")
    @PostMapping("/withdraw")
    @RequirePermission("account:withdraw")
    @RedisLock(key = "#req.accountId")
    @Idempotent(key = "#req.relatedBizNo")
    public R<AccountResp> withdraw(@Valid @RequestBody AccountOperationReq req) {
        accountAppService.withdraw(req.getAccountId(), req.getAmount(),
                req.getRelatedBizNo(), req.getSummary());
        ForexAccount account = accountAppService.getAccountDetail(req.getAccountId());
        return R.ok("取款成功", toAccountResp(account));
    }

    @Operation(summary = "冻结金额")
    @PostMapping("/freeze")
    @RequirePermission("account:freeze")
    @RedisLock(key = "#req.accountId")
    public R<AccountResp> freeze(@Valid @RequestBody AccountOperationReq req) {
        accountAppService.freeze(req.getAccountId(), req.getAmount());
        ForexAccount account = accountAppService.getAccountDetail(req.getAccountId());
        return R.ok("冻结成功", toAccountResp(account));
    }

    @Operation(summary = "解冻金额")
    @PostMapping("/unfreeze")
    @RequirePermission("account:unfreeze")
    @RedisLock(key = "#req.accountId")
    public R<AccountResp> unfreeze(@Valid @RequestBody AccountOperationReq req) {
        accountAppService.unfreeze(req.getAccountId(), req.getAmount());
        ForexAccount account = accountAppService.getAccountDetail(req.getAccountId());
        return R.ok("解冻成功", toAccountResp(account));
    }

    @Operation(summary = "查询账户交易流水")
    @GetMapping("/{accountId}/transactions")
    public R<PageResp<TransactionResp>> getTransactions(@PathVariable Long accountId,
                                                         @Valid PageReq pageReq) {
        PageResp<AccountTransaction> page = accountAppService.getTransactions(accountId, pageReq);
        List<TransactionResp> respList = page.getRecords().stream()
                .map(this::toTransactionResp)
                .toList();
        PageResp<TransactionResp> result = PageResp.of(
                page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    private AccountResp toAccountResp(ForexAccount account) {
        AccountResp resp = new AccountResp();
        resp.setId(account.getId());
        resp.setAccountNo(account.getAccountNo());
        resp.setCustomerId(account.getCustomerId());
        resp.setAccountType(account.getAccountType());
        resp.setCurrency(account.getCurrency());
        resp.setAccountName(account.getAccountName());
        resp.setBalance(account.getBalance());
        resp.setFrozenAmount(account.getFrozenAmount());
        resp.setAvailableBalance(account.getAvailableBalance());
        resp.setOpenDate(account.getOpenDate());
        resp.setOpenBranch(account.getOpenBranch());
        resp.setAccountStatus(account.getAccountStatus());
        resp.setInterestRate(account.getInterestRate());
        resp.setIsInterestBearing(account.getIsInterestBearing());
        resp.setCentralBankReportStatus(account.getCentralBankReportStatus());
        resp.setLastReportTime(account.getLastReportTime());
        return resp;
    }

    private TransactionResp toTransactionResp(AccountTransaction tx) {
        TransactionResp resp = new TransactionResp();
        resp.setId(tx.getId());
        resp.setTransactionNo(tx.getTransactionNo());
        resp.setAccountId(tx.getAccountId());
        resp.setAccountNo(tx.getAccountNo());
        resp.setTransactionType(tx.getTransactionType());
        resp.setAmount(tx.getAmount());
        resp.setCurrency(tx.getCurrency());
        resp.setBalanceBefore(tx.getBalanceBefore());
        resp.setBalanceAfter(tx.getBalanceAfter());
        resp.setRelatedBizNo(tx.getRelatedBizNo());
        resp.setRelatedBizType(tx.getRelatedBizType());
        resp.setTransactionTime(tx.getTransactionTime());
        resp.setSummary(tx.getSummary());
        return resp;
    }
}
