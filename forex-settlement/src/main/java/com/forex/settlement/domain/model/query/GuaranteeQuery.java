package com.forex.settlement.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GuaranteeQuery extends PageReq {

    private String guaranteeNo;
    private Long customerId;
    private String guaranteeStatus;
}
