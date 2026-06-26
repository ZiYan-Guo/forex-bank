package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ConfirmationPageQuery")
public class ConfirmationPageQuery {

    @Schema(description = "pageNum")
    private Integer pageNum;

    @Schema(description = "pageSize")
    private Integer pageSize;

}
