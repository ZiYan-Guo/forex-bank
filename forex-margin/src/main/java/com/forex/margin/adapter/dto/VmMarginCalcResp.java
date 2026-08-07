package com.forex.margin.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(description = "变动保证金计量结果")
public class VmMarginCalcResp {

    @Schema(description = "适用门槛值")
    private BigDecimal appliedThresholdAmount;

    @Schema(description = "保证金账户总余额=账户余额+在途金额")
    private BigDecimal totalAccountBalance;

    @Schema(description = "保证金交收净额，正数表示交付方向，负数表示返还方向")
    private BigDecimal netSettlementAmount;

    @Schema(description = "交付金额 Delivery Amount，正数")
    private BigDecimal deliveryAmount;

    @Schema(description = "返还金额 Return Amount，正数")
    private BigDecimal returnAmount;

    @Schema(description = "动作：NONE/DELIVERY/RETURN/MIXED")
    private String action;

    @Schema(description = "规则说明")
    private String ruleRemark;
}
