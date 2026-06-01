package com.forex.customer.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class CustomerReq {

    @Schema(description = "客户ID(更新时必填)")
    private Long customerId;

    @NotNull(message = "客户类型不能为空")
    @Schema(description = "客户类型", example = "1")
    private Integer customerType;

    @NotBlank(message = "客户名称不能为空")
    @Schema(description = "客户名称", example = "某科技有限公司")
    private String customerName;

    @Schema(description = "英文名称", example = "Tech Co., Ltd.")
    private String englishName;

    @NotBlank(message = "证件类型不能为空")
    @Schema(description = "证件类型", example = "USCC")
    private String certType;

    @NotBlank(message = "证件号码不能为空")
    @Schema(description = "证件号码", example = "91310000MA1XXXXXXX")
    private String certNo;

    @Schema(description = "国家代码", example = "CN")
    private String countryCode;

    @Schema(description = "地址", example = "上海市浦东新区")
    private String address;

    @Schema(description = "联系人", example = "张三")
    private String contactPerson;

    @Pattern(regexp = "^[0-9\\-+()]+$", message = "联系电话格式不正确")
    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "contact@example.com")
    private String email;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "风险等级")
    private Integer riskLevel;

    @Schema(description = "风险原因")
    private String riskReason;

    @Schema(description = "状态")
    private Integer status;
}
