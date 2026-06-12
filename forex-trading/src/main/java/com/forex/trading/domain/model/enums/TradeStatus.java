package com.forex.trading.domain.model.enums;

/**
 * 交易状态枚举
 * 定义交易生命周期的所有可能状态
 */
public enum TradeStatus {
    
    /**
     * 草稿 - 初始状态
     */
    DRAFT("DRAFT", "草稿"),
    
    /**
     * 已报价 - 等待客户确认
     */
    QUOTED("QUOTED", "已报价"),
    
    /**
     * 已确认 - 报价被接受
     */
    CONFIRMED("CONFIRMED", "已确认"),
    
    /**
     * 已执行 - 交易成功执行
     */
    EXECUTED("EXECUTED", "已执行"),
    
    /**
     * 已结算 - 资金已清算
     */
    SETTLED("SETTLED", "已结算"),
    
    /**
     * 已取消 - 交易被人工取消
     */
    CANCELLED("CANCELLED", "已取消"),
    
    /**
     * 已过期 - 报价超时未确认
     */
    EXPIRED("EXPIRED", "已过期");
    
    private final String code;
    private final String description;
    
    TradeStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static TradeStatus fromCode(String code) {
        for (TradeStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
