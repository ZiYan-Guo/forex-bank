package com.forex.account.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_forex_account")
public class ForexAccountPO extends BasePO {

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
}
