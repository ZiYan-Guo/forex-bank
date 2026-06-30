package com.forex.risk.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CompleteTaskReq")
public class CompleteTaskReq {
    @Schema(description = "result") private String result;
    @Schema(description = "comment") private String comment;
}
