package com.forex.settlement.domain.model.query;

import com.forex.common.base.dto.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CollectionQuery extends PageReq {

    private String collectionNo;
    private Long customerId;
    private String collectionStatus;
}
