package com.forex.saccr.adapter.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SimmCalculateCmd {
    private Long tradeId;
    private String tradeNo;
    private LocalDate calcDate;
    private BigDecimal notionalAmount;
}
