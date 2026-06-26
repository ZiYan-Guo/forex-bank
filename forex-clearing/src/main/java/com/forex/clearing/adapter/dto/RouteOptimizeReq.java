package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "RouteOptimizeReq")
public class RouteOptimizeReq {

    @Schema(description = "payCurrency")
    private String payCurrency;

    @Schema(description = "receiveCurrency")
    private String receiveCurrency;

    @Schema(description = "amount")
    private String amount;

    @Schema(description = "country")
    private String country;

}
