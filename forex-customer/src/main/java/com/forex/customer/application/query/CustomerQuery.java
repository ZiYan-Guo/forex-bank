package com.forex.customer.application.query;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerQuery extends PageReq {

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

    @Schema(description = "证件号码")
    private String certNo;
}
