package com.forex.saccr.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SA-CCR分页查询请求")
public class SaccrPageQuery extends PageReq {

    @Schema(description = "交易ID")
    private Long tradeId;

    @Schema(description = "交易编号")
    private String tradeNo;

    @Schema(description = "对手方ID")
    private String counterPartyId;

    @Schema(description = "计算方法")
    private String calcMethod;
}
