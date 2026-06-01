package com.forex.common.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CurrencyEnum {

    CNY("CNY", "人民币", "¥"),
    USD("USD", "美元", "$"),
    EUR("EUR", "欧元", "€"),
    JPY("JPY", "日元", "¥"),
    GBP("GBP", "英镑", "£"),
    HKD("HKD", "港币", "HK$"),
    AUD("AUD", "澳元", "A$"),
    CAD("CAD", "加元", "C$"),
    CHF("CHF", "瑞士法郎", "CHF"),
    SGD("SGD", "新加坡元", "S$"),
    KRW("KRW", "韩元", "₩"),
    NZD("NZD", "新西兰元", "NZ$"),
    SEK("SEK", "瑞典克朗", "kr"),
    DKK("DKK", "丹麦克朗", "kr"),
    NOK("NOK", "挪威克朗", "kr"),
    MOP("MOP", "澳门元", "MOP$"),
    THB("THB", "泰铢", "฿"),
    MYR("MYR", "林吉特", "RM"),
    RUB("RUB", "卢布", "₽"),
    ZAR("ZAR", "兰特", "R");

    private final String code;
    private final String name;
    private final String symbol;

    public static CurrencyEnum fromCode(String code) {
        for (CurrencyEnum c : values()) {
            if (c.code.equalsIgnoreCase(code)) {
                return c;
            }
        }
        return null;
    }
}
