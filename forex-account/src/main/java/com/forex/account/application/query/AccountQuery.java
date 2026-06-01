package com.forex.account.application.query;

import com.forex.common.base.dto.PageReq;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountQuery extends PageReq {

    private Long customerId;
    private String accountNo;
    private String accountStatus;
    private String currency;
}
