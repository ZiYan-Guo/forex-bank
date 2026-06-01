package com.forex.bookkeeping.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EntryResp {

    @Schema(description = "分录ID")
    private Long id;

    @Schema(description = "凭证号")
    private String voucherNo;

    @Schema(description = "凭证日期")
    private LocalDate voucherDate;

    @Schema(description = "会计期间")
    private String fiscalPeriod;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务编号")
    private String bizNo;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "分录方向")
    private String entryDirection;

    @Schema(description = "科目代码")
    private String accountCode;

    @Schema(description = "科目名称")
    private String accountName;

    @Schema(description = "对方科目代码")
    private String oppositeAccountCode;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "分录状态")
    private String entryStatus;

    @Schema(description = "冲正凭证号")
    private String reversedVoucherNo;

    @Schema(description = "过账时间")
    private LocalDateTime postedTime;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
