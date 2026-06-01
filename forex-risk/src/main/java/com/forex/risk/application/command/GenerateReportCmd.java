package com.forex.risk.application.command;

import lombok.Data;

@Data
public class GenerateReportCmd {

    private String reportType;
    private String reportPeriod;
    private Long customerId;
}
