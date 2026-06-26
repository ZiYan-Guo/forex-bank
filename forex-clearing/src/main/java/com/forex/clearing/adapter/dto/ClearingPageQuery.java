package com.forex.clearing.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "清算指令分页查询请求")
public class ClearingPageQuery extends PageReq {

    @Schema(description = "指令编号")
    private String instructionNo;

    @Schema(description = "清算渠道")
    private String clearingChannel;

    @Schema(description = "指令状态")
    private String instructionStatus;

    @Schema(description = "起息日")
    private LocalDate valueDate;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务编号")
    private String bizNo;

    @Schema(description = "结算类型")
    private String settlementType;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
