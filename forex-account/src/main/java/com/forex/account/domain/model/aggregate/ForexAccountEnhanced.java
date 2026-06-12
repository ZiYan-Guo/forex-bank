package com.forex.account.domain.model.aggregate;

import com.forex.common.base.BaseAggregate;
import com.forex.common.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * 增强型外汇账户聚合根
 * 完善业务规则验证
 */
@Getter
public class ForexAccountEnhanced extends BaseAggregate {
    
    private static final Set<String> VALID_ACCOUNT_TYPES = 
        Set.of("CHECKING", "SAVINGS", "INVESTMENT", "CLEARING");
    
    private static final Set<String> VALID_CURRENCIES = 
        Set.of("USD", "EUR", "GBP", "JPY", "CNY", "HKD", "SGD", "AUD", "CAD");
    
    private static final Set<String> VALID_STATUSES = 
        Set.of("NORMAL", "FROZEN", "CLOSED");
    
    private Long customerId;
    private String accountNo;
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
    
    private ForexAccountEnhanced() {
        super();
    }
    
    /**
     * 创建新账户
     */
    public static ForexAccountEnhanced create(Long customerId, String accountType, 
                                               String currency, String accountName, 
                                               String openBranch) {
        ForexAccountEnhanced account = new ForexAccountEnhanced();
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
    
    /**
     * 从数据库重建聚合
     */
    public static ForexAccountEnhanced reconstitute(Long id, String accountNo, Long customerId,
                                                     String accountType, String currency,
                                                     String accountName, BigDecimal balance,
                                                     BigDecimal frozenAmount, BigDecimal availableBalance,
                                                     LocalDate openDate, String openBranch,
                                                     String accountStatus, BigDecimal interestRate,
                                                     Integer isInterestBearing, Integer centralBankReportStatus) {
        ForexAccountEnhanced account = new ForexAccountEnhanced();
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
        return account;
    }
    
    /**
     * 存入资金
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("E101", "存入金额必须大于0");
        }
        if (!"NORMAL".equals(accountStatus)) {
            throw new BusinessException("E102", "账户状态异常，无法存入资金");
        }
        this.balance = this.balance.add(amount);
        this.availableBalance = calculateAvailableBalance();
        markUpdated();
    }
    
    /**
     * 取出资金
     */
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("E103", "取款金额必须大于0");
        }
        if (!"NORMAL".equals(accountStatus)) {
            throw new BusinessException("E104", "账户状态异常，无法取出资金");
        }
        BigDecimal available = calculateAvailableBalance();
        if (available.compareTo(amount) < 0) {
            throw new BusinessException("E105", "可用余额不足，无法取出");
        }
        this.balance = this.balance.subtract(amount);
        this.availableBalance = calculateAvailableBalance();
        markUpdated();
    }
    
    /**
     * 冻结资金
     */
    public void freeze(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("E106", "冻结金额必须大于0");
        }
        BigDecimal available = calculateAvailableBalance();
        if (available.compareTo(amount) < 0) {
            throw new BusinessException("E107", "可用余额不足，无法冻结");
        }
        this.frozenAmount = this.frozenAmount.add(amount);
        this.availableBalance = calculateAvailableBalance();
        markUpdated();
    }
    
    /**
     * 解冻资金
     */
    public void unfreeze(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("E108", "解冻金额必须大于0");
        }
        if (this.frozenAmount.compareTo(amount) < 0) {
            throw new BusinessException("E109", "冻结金额不足，无法解冻");
        }
        this.frozenAmount = this.frozenAmount.subtract(amount);
        this.availableBalance = calculateAvailableBalance();
        markUpdated();
    }
    
    /**
     * 关闭账户
     */
    public void close() {
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("E110", "账户余额不为零，无法关闭");
        }
        this.accountStatus = "CLOSED";
        markUpdated();
    }
    
    /**
     * 冻结账户
     */
    public void setFrozen() {
        this.accountStatus = "FROZEN";
        markUpdated();
    }
    
    /**
     * 解冻账户
     */
    public void setNormal() {
        this.accountStatus = "NORMAL";
        markUpdated();
    }
    
    /**
     * 计算可用余额
     */
    public BigDecimal calculateAvailableBalance() {
        return this.balance.subtract(this.frozenAmount);
    }
    
    /**
     * 分配账户号
     */
    public void assignAccountNo(String accountNo) {
        if (accountNo == null || accountNo.isEmpty()) {
            throw new BusinessException("E111", "账户号不能为空");
        }
        this.accountNo = accountNo;
        markUpdated();
    }
    
    /**
     * 业务规则验证
     */
    @Override
    protected void validate() {
        if (customerId == null) {
            throw new BusinessException("E201", "客户ID不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new BusinessException("E202", "币种不能为空");
        }
        if (!VALID_CURRENCIES.contains(currency)) {
            throw new BusinessException("E203", "无效的币种: " + currency);
        }
        if (accountType == null || accountType.isBlank()) {
            throw new BusinessException("E204", "账户类型不能为空");
        }
        if (!VALID_ACCOUNT_TYPES.contains(accountType)) {
            throw new BusinessException("E205", "无效的账户类型: " + accountType);
        }
        if (!VALID_STATUSES.contains(accountStatus)) {
            throw new BusinessException("E206", "无效的账户状态: " + accountStatus);
        }
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("E207", "余额不能为负数");
        }
        if (frozenAmount == null || frozenAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("E208", "冻结金额不能为负数");
        }
        if (frozenAmount.compareTo(balance) > 0) {
            throw new BusinessException("E209", "冻结金额不能大于余额");
        }
    }
}
