package com.forex.customer.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Customer extends BaseAggregate {

    private Long id;
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

    private Customer() {
        super();
    }

    public static Customer create(String customerNo, Integer customerType, String customerName,
                                   String englishName, String certType, String certNo,
                                   String countryCode, String address, String contactPerson,
                                   String contactPhone, String email, Integer riskLevel,
                                   String riskReason, String crossBorderPlatformId, String remark) {
        Customer customer = new Customer();
        customer.customerNo = customerNo;
        customer.customerType = customerType;
        customer.customerName = customerName;
        customer.englishName = englishName;
        customer.certType = certType;
        customer.certNo = certNo;
        customer.countryCode = countryCode;
        customer.address = address;
        customer.contactPerson = contactPerson;
        customer.contactPhone = contactPhone;
        customer.email = email;
        customer.riskLevel = riskLevel;
        customer.riskReason = riskReason;
        customer.crossBorderPlatformId = crossBorderPlatformId;
        customer.remark = remark;
        customer.status = 1;
        customer.dueDiligenceStatus = 0;
        customer.validate();
        return customer;
    }

    public static Customer reconstitute(Long id, String customerNo, Integer customerType,
                                         String customerName, String englishName, String certType,
                                         String certNo, String countryCode, String address,
                                         String contactPerson, String contactPhone, String email,
                                         Integer riskLevel, String riskReason, Integer dueDiligenceStatus,
                                         LocalDateTime dueDiligenceDate, String crossBorderPlatformId,
                                         Integer status, String remark) {
        Customer customer = new Customer();
        customer.id = id;
        customer.customerNo = customerNo;
        customer.customerType = customerType;
        customer.customerName = customerName;
        customer.englishName = englishName;
        customer.certType = certType;
        customer.certNo = certNo;
        customer.countryCode = countryCode;
        customer.address = address;
        customer.contactPerson = contactPerson;
        customer.contactPhone = contactPhone;
        customer.email = email;
        customer.riskLevel = riskLevel;
        customer.riskReason = riskReason;
        customer.dueDiligenceStatus = dueDiligenceStatus;
        customer.dueDiligenceDate = dueDiligenceDate;
        customer.crossBorderPlatformId = crossBorderPlatformId;
        customer.status = status;
        customer.remark = remark;
        return customer;
    }

    public void updateRiskLevel(Integer newLevel, String reason) {
        this.riskLevel = newLevel;
        this.riskReason = reason;
        markUpdated();
    }

    public void enable() {
        this.status = 1;
        markUpdated();
    }

    public void disable() {
        this.status = 0;
        markUpdated();
    }

    public void completeDueDiligence() {
        this.dueDiligenceStatus = 1;
        this.dueDiligenceDate = LocalDateTime.now();
        markUpdated();
    }

    public boolean isActive() {
        return this.status != null && this.status == 1;
    }

    public boolean isDueDiligenceCompleted() {
        return this.dueDiligenceStatus != null && this.dueDiligenceStatus == 1;
    }

    @Override
    protected void validate() {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("客户名称不能为空");
        }
        if (customerType == null) {
            throw new IllegalArgumentException("客户类型不能为空");
        }
    }
}
