package com.forex.cashpool.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "境外放款分页查询请求")
public class LendingPageQuery {
    @Schema(description = "页码") private Integer pageNum;
    @Schema(description = "每页大小") private Integer pageSize;
}
