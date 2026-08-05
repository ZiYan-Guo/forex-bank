package com.forex.settlement.adapter.dto;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "保函分页查询请求")
public class GuaranteePageQuery extends PageReq {

    @Schema(description = "保函编号")
    private String guaranteeNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "保函状态")
    private String guaranteeStatus;
}
