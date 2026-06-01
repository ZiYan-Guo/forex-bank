package com.forex.account.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AccountResp {

    @Schema(description = "账户ID")
    private Long id;

    @Schema(description = "账号")
    private String accountNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "账户名称")
    private String accountName;

    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "冻结金额")
    private BigDecimal frozenAmount;

    @Schema(description = "可用余额")
    private BigDecimal availableBalance;

    @Schema(description = "开户日期")
    private LocalDate openDate;

    @Schema(description = "开户网点")
    private String openBranch;

    @Schema(description = "账户状态")
    private String accountStatus;

    @Schema(description = "利率")
    private BigDecimal interestRate;

    @Schema(description = "是否计息")
    private Integer isInterestBearing;

    @Schema(description = "央行报送状态")
    private Integer centralBankReportStatus;

    @Schema(description = "最后报送时间")
    private LocalDateTime lastReportTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
