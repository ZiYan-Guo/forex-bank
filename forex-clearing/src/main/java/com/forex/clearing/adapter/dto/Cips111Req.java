package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "CIPS.111报文生成请求")
public class Cips111Req {

    @Schema(description = "报文ID")
    private String msgId;

    @Schema(description = "汇款人名称")
    private String debtorName;

    @Schema(description = "汇款人账号")
    private String debtorAcct;

    @Schema(description = "收款人名称")
    private String creditorName;

    @Schema(description = "收款人账号")
    private String creditorAcct;

    @Schema(description = "收款人CIPS ID")
    private String creditorCipsId;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "汇款附言")
    private String remittanceInfo;
}
