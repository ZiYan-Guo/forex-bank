package com.forex.position.application.command;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionCmd {

    private String currencyPair;
    private String positionType;
    private String positionCurrency;
    private BigDecimal longAmount;
    private BigDecimal shortAmount;
    private Long traderId;
    private String branchCode;
}
