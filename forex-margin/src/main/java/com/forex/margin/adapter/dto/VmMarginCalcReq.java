package com.forex.margin.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "变动保证金计量请求")
public class VmMarginCalcReq {

    @NotNull(message = "敞口金额不能为空")
    @Schema(description = "交易组合敞口金额/估值金额，正数取交易对手阈值，负数取我方阈值")
    private BigDecimal exposureAmount;

    @Schema(description = "我方门槛值金额")
    private BigDecimal ourThresholdAmount = BigDecimal.ZERO;

    @Schema(description = "交易对手方门槛值金额")
    private BigDecimal counterpartyThresholdAmount = BigDecimal.ZERO;

    @Schema(description = "最小转让金额")
    private BigDecimal minimumTransferAmount = BigDecimal.ZERO;

    @Schema(description = "保证金账户余额")
    private BigDecimal accountBalance = BigDecimal.ZERO;

    @Schema(description = "保证金在途金额")
    private BigDecimal inTransitAmount = BigDecimal.ZERO;

    @Schema(description = "交付取整单位，空值表示不取整")
    private BigDecimal deliveryRoundingUnit;

    @Schema(description = "返还取整单位，空值表示不取整")
    private BigDecimal returnRoundingUnit;
}
