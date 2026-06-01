package com.forex.settlement.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class LcQuery extends PageReq {

    private String lcNo;
    private Long customerId;
    private String lcType;
    private String lcStatus;
    private LocalDate startDate;
    private LocalDate endDate;
}
