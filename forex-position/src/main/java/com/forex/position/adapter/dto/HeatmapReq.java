package com.forex.position.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "HeatmapReq")
public class HeatmapReq {

    @Schema(description = "date")
    private String date;

}
