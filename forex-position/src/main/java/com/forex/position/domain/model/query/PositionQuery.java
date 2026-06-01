package com.forex.position.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class PositionQuery extends PageReq {

    private String currencyPair;
    private String positionType;
    private String positionCurrency;
    private LocalDate positionDate;
    private String riskLevel;
    private Long traderId;
}
