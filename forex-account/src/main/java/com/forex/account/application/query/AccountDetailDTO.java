package com.forex.account.application.query;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AccountDetailDTO {

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
    private LocalDateTime createTime;
}
