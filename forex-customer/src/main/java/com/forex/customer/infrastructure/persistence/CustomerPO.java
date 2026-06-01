package com.forex.customer.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_customer")
public class CustomerPO extends BasePO {

    private String customerNo;
    private Integer customerType;
    private String customerName;
    private String englishName;
    private String certType;
    private String certNo;
    private String countryCode;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String email;
    private Integer riskLevel;
    private String riskReason;
    private Integer dueDiligenceStatus;
    private LocalDateTime dueDiligenceDate;
    private String crossBorderPlatformId;
    private Integer status;
    private String remark;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private String createBy;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private String updateBy;
}
