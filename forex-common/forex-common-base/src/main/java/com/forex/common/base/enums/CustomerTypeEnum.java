package com.forex.common.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerTypeEnum {

    CORPORATE(1, "对公"),
    PRIVATE(2, "对私"),
    INTERBANK(3, "同业");

    private final int code;
    private final String desc;
}
