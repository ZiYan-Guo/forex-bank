package com.forex.clearing.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClearingQuery extends PageReq {

    private String instructionNo;
    private String clearingChannel;
    private String instructionStatus;
    private LocalDate valueDate;
    private String bizType;
    private String bizNo;
    private String settlementType;
    private LocalDate startDate;
    private LocalDate endDate;
}
