package com.forex.cashpool.adapter.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CreatePoolReq")
public class CreatePoolReq {
    @Schema(description = "poolName") private String poolName;
    @Schema(description = "poolCurrency") private String poolCurrency;
    @Schema(description = "totalLimit") private BigDecimal totalLimit;
    @Schema(description = "mainAccountId") private Long mainAccountId;
}
