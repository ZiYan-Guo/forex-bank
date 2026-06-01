package com.forex.common.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentDirectionEnum {

    INWARD(1, "汇入"),
    OUTWARD(2, "汇出");

    private final int code;
    private final String desc;
}
