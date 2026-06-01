package com.forex.risk.application.command;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EvaluateCmd {

    private Long customerId;
    private String bizType;
    private String bizNo;
    private BigDecimal transactionAmount;
    private String transactionCurrency;
    private LocalDateTime transactionTime;
}
