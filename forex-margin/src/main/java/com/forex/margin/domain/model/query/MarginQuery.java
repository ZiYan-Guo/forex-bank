package com.forex.margin.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MarginQuery extends PageReq {

    private Long customerId;
    private Long tradeId;
    private String marginNo;
    private String marginType;
    private String status;
}
