package com.forex.trading.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RollOverReq {

    @NotBlank(message = "交易编号不能为空")
    @Schema(description = "交易编号", example = "FX20250601001")
    private String tradeNo;

    @Schema(description = "新到期日", example = "2025-08-01")
    private LocalDate newMaturityDate;

    @Schema(description = "新汇率", example = "7.2000")
    private BigDecimal newRate;
}
