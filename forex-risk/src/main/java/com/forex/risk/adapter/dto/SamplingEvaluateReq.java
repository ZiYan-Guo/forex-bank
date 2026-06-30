package com.forex.risk.adapter.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "SamplingEvaluateReq")
public class SamplingEvaluateReq {
    @Schema(description = "customerId") private Long customerId;
    @Schema(description = "bizType") private String bizType;
    @Schema(description = "amount") private BigDecimal amount;
    @Schema(description = "currency") private String currency;
    @Schema(description = "countryCode") private String countryCode;
}
