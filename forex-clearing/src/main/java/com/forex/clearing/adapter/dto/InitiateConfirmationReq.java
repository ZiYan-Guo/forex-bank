package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "InitiateConfirmationReq")
public class InitiateConfirmationReq {

    @Schema(description = "tradeNo")
    private String tradeNo;

    @Schema(description = "tradeType")
    private String tradeType;

    @Schema(description = "currencyPair")
    private String currencyPair;

    @Schema(description = "amount")
    private String amount;

    @Schema(description = "rate")
    private String rate;

    @Schema(description = "valueDate")
    private String valueDate;

    @Schema(description = "counterparty")
    private String counterparty;

}
