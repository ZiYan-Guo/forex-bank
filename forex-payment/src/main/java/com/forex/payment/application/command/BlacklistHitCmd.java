package com.forex.payment.application.command;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BlacklistHitCmd {

    private String hitType;
    private String hitListName;
    private String hitField;
    private String hitValue;
    private BigDecimal matchScore;
    private String checkResult;
}
