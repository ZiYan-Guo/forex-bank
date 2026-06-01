package com.forex.account.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResp {

    @Schema(description = "交易ID")
    private Long id;

    @Schema(description = "交易流水号")
    private String transactionNo;

    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "账号")
    private String accountNo;

    @Schema(description = "交易类型")
    private String transactionType;

    @Schema(description = "交易金额")
    private BigDecimal amount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "交易前余额")
    private BigDecimal balanceBefore;

    @Schema(description = "交易后余额")
    private BigDecimal balanceAfter;

    @Schema(description = "关联业务单号")
    private String relatedBizNo;

    @Schema(description = "关联业务类型")
    private String relatedBizType;

    @Schema(description = "交易时间")
    private LocalDateTime transactionTime;

    @Schema(description = "摘要")
    private String summary;
}
