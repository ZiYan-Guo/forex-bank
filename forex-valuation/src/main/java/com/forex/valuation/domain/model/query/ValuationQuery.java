package com.forex.valuation.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValuationQuery extends PageReq {

    private Long tradeId;
    private String tradeType;
    private String currencyPair;
    private LocalDate startDate;
    private LocalDate endDate;
}
