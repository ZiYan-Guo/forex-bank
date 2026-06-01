package com.forex.payment.domain.model.dto;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentQuery extends PageReq {

    private String paymentNo;
    private Long customerId;
    private String paymentDirection;
    private String paymentType;
    private String payCurrency;
    private String paymentStatus;
    private String chargeBearer;
    private LocalDate startDate;
    private LocalDate endDate;
    private String keyword;
}
