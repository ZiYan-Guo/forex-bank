package com.forex.reporting.adapter.dto;

import com.forex.common.base.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "监管报送分页查询请求")
public class ReportPageQuery extends PageReq {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "报告类型")
    private String reportType;

    @Schema(description = "报告状态")
    private String reportStatus;

    @Schema(description = "交易日期")
    private LocalDate transactionDate;

    @Schema(description = "交易编号")
    private String transactionNo;
}
