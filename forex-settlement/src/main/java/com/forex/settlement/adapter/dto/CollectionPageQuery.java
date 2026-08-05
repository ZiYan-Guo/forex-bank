package com.forex.settlement.adapter.dto;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "托收分页查询请求")
public class CollectionPageQuery extends PageReq {

    @Schema(description = "托收编号")
    private String collectionNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "托收状态")
    private String collectionStatus;
}
