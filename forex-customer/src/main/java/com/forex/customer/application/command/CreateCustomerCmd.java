package com.forex.customer.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class CreateCustomerCmd {

    @NotNull(message = "客户类型不能为空")
    private Integer customerType;

    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    private String englishName;

    @NotBlank(message = "证件类型不能为空")
    private String certType;

    @NotBlank(message = "证件号码不能为空")
    private String certNo;

    private String countryCode;

    private String address;

    private String contactPerson;

    @Pattern(regexp = "^[0-9\\-+()]+$", message = "联系电话格式不正确")
    private String contactPhone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String remark;
}
