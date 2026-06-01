package com.forex.saccr.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SaccrQuery extends PageReq {

    private Long tradeId;
    private String tradeNo;
    private String counterPartyId;
    private String calcMethod;
}
