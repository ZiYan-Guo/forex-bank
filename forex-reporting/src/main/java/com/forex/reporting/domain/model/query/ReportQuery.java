package com.forex.reporting.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportQuery extends PageReq {

    private Long customerId;
    private String reportType;
    private String reportStatus;
    private LocalDate transactionDate;
    private String transactionNo;
}
