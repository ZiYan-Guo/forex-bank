package com.forex.clearing.domain.model.valueobject;

/** FX trade confirmation type per PBOC JR/T 0310-2025. 外汇交易确认类型。 */
public enum ConfirmationFlag {
    CENTRALIZED("集中确认", "CFETS Infrastructure Service Platform"),
    BILATERAL("双边确认", "SWIFT system");

    private final String name;
    private final String platform;

    ConfirmationFlag(String name, String platform) {
        this.name = name;
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }
}
