package com.forex.settlement.application.command;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AmendLcCmd {

    private String lcNo;
    private LocalDate newExpiryDate;
    private BigDecimal newAmount;
    private String amendmentReason;
}
