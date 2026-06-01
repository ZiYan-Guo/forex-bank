package com.forex.customer.application.query;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
public class CustomerListDTO {

    @Schema(description = "客户ID")
    private Long id;

    @Schema(description = "客户编号")
    private String customerNo;

    @Schema(description = "客户类型")
    private Integer customerType;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "风险等级")
    private Integer riskLevel;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "信用额度摘要")
    private String creditLimitsSummary;
}
