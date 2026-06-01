package com.forex.customer.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CustomerRegInfo {

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
    private String crossBorderPlatformId;
    private String remark;
}
