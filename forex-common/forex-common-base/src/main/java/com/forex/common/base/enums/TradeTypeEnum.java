package com.forex.common.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeTypeEnum {

    SPOT("SPOT", "即期"),
    FORWARD("FORWARD", "远期"),
    SWAP("SWAP", "掉期"),
    OPTION("OPTION", "期权"),
    OPTION_DATE("OPTION_DATE", "择期");

    private final String code;
    private final String desc;
}
