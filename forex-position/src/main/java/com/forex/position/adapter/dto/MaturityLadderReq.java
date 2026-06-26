package com.forex.position.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "MaturityLadderReq")
public class MaturityLadderReq {

    @Schema(description = "date")
    private String date;

}
