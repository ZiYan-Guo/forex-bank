package com.forex.cashpool.adapter.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CalculateQuotaReq")
public class CalculateQuotaReq {
    @Schema(description = "netAssets") private BigDecimal netAssets;
}
