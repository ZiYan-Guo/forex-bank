package com.forex.saccr.adapter.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SaccrResultResp {
    private Long id;
    private String calcNo;
    private Long tradeId;
    private String tradeNo;
    private String counterPartyId;
    private LocalDate calcDate;
    private BigDecimal rc;
    private BigDecimal pfe;
    private BigDecimal exposure;
    private BigDecimal alpha;
    private String calcMethod;
    private String resultJson;
}
