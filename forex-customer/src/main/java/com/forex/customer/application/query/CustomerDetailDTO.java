package com.forex.customer.application.query;

import com.forex.customer.domain.model.entity.CreditLimit;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerDetailDTO {

    @Schema(description = "客户ID")
    private Long id;

    @Schema(description = "客户编号")
    private String customerNo;

    @Schema(description = "客户类型")
    private Integer customerType;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "英文名称")
    private String englishName;

    @Schema(description = "证件类型")
    private String certType;

    @Schema(description = "证件号码")
    private String certNo;

    @Schema(description = "国家代码")
    private String countryCode;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "风险等级")
    private Integer riskLevel;

    @Schema(description = "风险原因")
    private String riskReason;

    @Schema(description = "尽职调查状态")
    private Integer dueDiligenceStatus;

    @Schema(description = "尽职调查日期")
    private LocalDateTime dueDiligenceDate;

    @Schema(description = "跨境平台ID")
    private String crossBorderPlatformId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "信用额度列表")
    private List<CreditLimit> limits;
}
