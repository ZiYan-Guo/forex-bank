package com.forex.saccr.adapter.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SimmResultResp {
    private Long id;
    private String calcNo;
    private Long tradeId;
    private String tradeNo;
    private LocalDate calcDate;
    private BigDecimal notionalAmount;
    private BigDecimal deltaMargin;
    private BigDecimal vegaMargin;
    private BigDecimal curvatureMargin;
    private BigDecimal totalMargin;
    private String calcMethod;
    private String sensitivitiesJson;
}
