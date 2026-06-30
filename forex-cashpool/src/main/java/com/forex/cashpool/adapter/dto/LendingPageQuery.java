package com.forex.cashpool.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "LendingPageQuery")
public class LendingPageQuery {
    @Schema(description = "pageNum") private Integer pageNum;
    @Schema(description = "pageSize") private Integer pageSize;
}
