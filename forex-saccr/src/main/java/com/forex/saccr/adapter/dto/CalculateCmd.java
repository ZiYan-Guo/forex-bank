package com.forex.saccr.adapter.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CalculateCmd {
    private Long tradeId;
    private String tradeNo;
    private String counterPartyId;
    private LocalDate calcDate;
    private BigDecimal rc;
    private BigDecimal pfe;
}
