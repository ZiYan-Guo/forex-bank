package com.forex.preciousmetal.application.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderQuery extends PageReq {
    private Long customerId;
    private String metalType;
    private String tradeType;
    private String direction;
    private String orderStatus;
    private LocalDate startDate;
    private LocalDate endDate;
}
