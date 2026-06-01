package com.forex.common.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageReq {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "20")
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 200, message = "每页条数最大为200")
    private Integer pageSize = 20;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "排序方向: asc/desc")
    private String orderDirection;

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
