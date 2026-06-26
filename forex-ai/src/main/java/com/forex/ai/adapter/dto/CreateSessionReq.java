package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CreateSessionReq")
public class CreateSessionReq {

    @Schema(description = "userId")
    private String userId;

}
