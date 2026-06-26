package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "NlTradeReq")
public class NlTradeReq {

    @Schema(description = "query")
    private String query;

}
