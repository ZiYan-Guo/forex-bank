package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ResolveConfirmationReq")
public class ResolveConfirmationReq {

    @Schema(description = "action")
    private String action;

    @Schema(description = "comment")
    private String comment;

    @Schema(description = "operatorId")
    private Long operatorId;

}
