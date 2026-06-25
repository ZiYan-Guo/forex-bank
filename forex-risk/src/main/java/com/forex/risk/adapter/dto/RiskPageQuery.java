package com.forex.risk.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "风险日志分页查询请求")
public class RiskPageQuery extends PageReq {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "风险类别")
    private String riskCategory;

    @Schema(description = "风险级别")
    private String riskLevel;

    @Schema(description = "检查结果")
    private String checkResult;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
