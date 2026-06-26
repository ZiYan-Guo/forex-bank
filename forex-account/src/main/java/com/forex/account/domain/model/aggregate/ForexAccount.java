package com.forex.account.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.enums.CurrencyEnum;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class ForexAccount extends BaseAggregate {

    private Long id;
    private String accountNo;
    private Long customerId;
    private String accountType;
    private String currency;
    private String accountName;
    private BigDecimal balance;
    private BigDecimal frozenAmount;
    private BigDecimal availableBalance;
    private LocalDate openDate;
    private String openBranch;
    private String accountStatus;
    private BigDecimal interestRate;
    private Integer isInterestBearing;
    private Integer centralBankReportStatus;
    private LocalDateTime lastReportTime;

    private ForexAccount() {
        super();
    }

    public static ForexAccount create(Long customerId, String accountType, String currency,
                                       String accountName, String openBranch) {
        ForexAccount account = new ForexAccount();
        account.customerId = customerId;
        account.accountType = accountType;
        account.currency = currency;
        account.accountName = accountName;
        account.openBranch = openBranch;
        account.balance = BigDecimal.ZERO;
        account.frozenAmount = BigDecimal.ZERO;
        account.availableBalance = BigDecimal.ZERO;
        account.openDate = LocalDate.now();
        account.accountStatus = "NORMAL";
        account.interestRate = BigDecimal.ZERO;
        account.isInterestBearing = 0;
        account.centralBankReportStatus = 0;
        account.validate();
        return account;
    }

    public static ForexAccount reconstitute(Long id, String accountNo, Long customerId,
                                             String accountType, String currency,
                                             String accountName, BigDecimal balance,
                                             BigDecimal frozenAmount, BigDecimal availableBalance,
                                             LocalDate openDate, String openBranch,
                                             String accountStatus, BigDecimal interestRate,
                                             Integer isInterestBearing, Integer centralBankReportStatus,
                                             LocalDateTime lastReportTime) {
        ForexAccount account = new ForexAccount();
        account.id = id;
        account.accountNo = accountNo;
        account.customerId = customerId;
        account.accountType = accountType;
        account.currency = currency;
        account.accountName = accountName;
        account.balance = balance;
        account.frozenAmount = frozenAmount;
        account.availableBalance = availableBalance;
        account.openDate = openDate;
        account.openBranch = openBranch;
        account.accountStatus = accountStatus;
        account.interestRate = interestRate;
        account.isInterestBearing = isInterestBearing;
        account.centralBankReportStatus = centralBankReportStatus;
        account.lastReportTime = lastReportTime;
        return account;
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "存入金额必须大于0");
        }
        this.balance = this.balance.add(amount);
        this.availableBalance = calculateAvailableBalance();
        markUpdated();
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "取款金额必须大于0");
        }
        BigDecimal available = calculateAvailableBalance();
        if (available.compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "可用余额不足");
        }
        this.balance = this.balance.subtract(amount);
        this.availableBalance = calculateAvailableBalance();
        markUpdated();
    }

    public void freeze(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "冻结金额必须大于0");
        }
        BigDecimal available = calculateAvailableBalance();
        if (available.compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "可用余额不足，无法冻结");
        }
        this.frozenAmount = this.frozenAmount.add(amount);
        this.availableBalance = calculateAvailableBalance();
        markUpdated();
    }

    public void unfreeze(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "解冻金额必须大于0");
        }
        if (this.frozenAmount.compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "冻结金额不足，无法解冻");
        }
        this.frozenAmount = this.frozenAmount.subtract(amount);
        this.availableBalance = calculateAvailableBalance();
        markUpdated();
    }

    public void close() {
        this.accountStatus = "CLOSED";
        markUpdated();
    }

    public BigDecimal calculateAvailableBalance() {
        return this.balance.subtract(this.frozenAmount);
    }

    public void assignAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public void setFrozen() {
        this.accountStatus = "FROZEN";
        markUpdated();
    }

    public void setNormal() {
        this.accountStatus = "NORMAL";
        markUpdated();
    }

    @Override
    protected void validate() {
        if (customerId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "客户ID不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "币种不能为空");
        }
        if (CurrencyEnum.fromCode(currency) == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "无效的币种: " + currency);
        }
    }
}
