package com.forex.settlement.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "信用证分页查询请求")
public class LcPageQuery extends PageReq {

    @Schema(description = "信用证编号")
    private String lcNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "信用证类型")
    private String lcType;

    @Schema(description = "信用证状态")
    private String lcStatus;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
