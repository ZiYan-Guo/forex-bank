package com.forex.exchange.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExchangeOrderQuery extends PageReq {

    private Long customerId;
    private String orderNo;
    private String orderType;
    private String dealType;
    private String orderStatus;
    private String baseCurrency;
    private String quoteCurrency;
}
