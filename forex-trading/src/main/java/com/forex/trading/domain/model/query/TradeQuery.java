package com.forex.trading.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class TradeQuery extends PageReq {

    private String tradeNo;
    private Long customerId;
    private String tradeType;
    private String tradeStatus;
    private String dealType;
    private LocalDate startDate;
    private LocalDate endDate;
}
