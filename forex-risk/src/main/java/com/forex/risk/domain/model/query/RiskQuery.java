package com.forex.risk.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class RiskQuery extends PageReq {

    private Long customerId;
    private String bizType;
    private String riskCategory;
    private String riskLevel;
    private String checkResult;
    private LocalDate startDate;
    private LocalDate endDate;
}
