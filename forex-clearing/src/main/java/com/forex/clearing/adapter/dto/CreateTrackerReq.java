package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CreateTrackerReq")
public class CreateTrackerReq {

    @Schema(description = "paymentNo")
    private String paymentNo;

    @Schema(description = "instructionNo")
    private String instructionNo;

    @Schema(description = "channel")
    private String channel;

}
