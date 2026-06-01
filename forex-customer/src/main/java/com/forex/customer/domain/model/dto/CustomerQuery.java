package com.forex.customer.domain.model.dto;

import lombok.Data;

@Data
public class CustomerQuery {

    private String customerNo;
    private String customerName;
    private Integer customerType;
    private String certType;
    private String certNo;
    private String countryCode;
    private Integer riskLevel;
    private Integer status;
    private Integer dueDiligenceStatus;
    private String keyword;
}
