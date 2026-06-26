package com.forex.margin.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "保证金分页查询请求")
public class MarginPageQuery extends PageReq {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "交易ID")
    private Long tradeId;

    @Schema(description = "保证金编号")
    private String marginNo;

    @Schema(description = "保证金类型")
    private String marginType;

    @Schema(description = "状态")
    private String status;
}
