package com.forex.cashpool.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "额度计算请求")
public class CalculateQuotaReq {
    @Schema(description = "净资产") private BigDecimal netAssets;
}
