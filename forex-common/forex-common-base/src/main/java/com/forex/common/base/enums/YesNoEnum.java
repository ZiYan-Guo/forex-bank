package com.forex.common.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum YesNoEnum {

    YES(1, "是"),
    NO(0, "否");

    private final int code;
    private final String desc;
}
