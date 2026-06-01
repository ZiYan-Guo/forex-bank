package com.forex.customer.application.command;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateCustomerCmd {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    private String customerName;

    private String englishName;

    private String address;

    private String contactPerson;

    private String contactPhone;

    private String email;

    private Integer riskLevel;

    private String riskReason;

    private Integer status;

    private String remark;
}
