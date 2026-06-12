package com.forex.common.test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 测试数据构建器
 * 方便创建测试用例的各类对象
 */
public class TestDataBuilder {
    
    /**
     * 创建默认的客户 ID
     */
    public static Long defaultCustomerId() {
        return 1000001L;
    }
    
    /**
     * 创建默认的用户 ID
     */
    public static Long defaultUserId() {
        return 1L;
    }
    
    /**
     * 创建默认金额
     */
    public static BigDecimal defaultAmount() {
        return new BigDecimal("10000.00");
    }
    
    /**
     * 创建默认汇率
     */
    public static BigDecimal defaultRate() {
        return new BigDecimal("6.50");
    }
    
    /**
     * 创建默认起息日
     */
    public static LocalDate defaultValueDate() {
        return LocalDate.now().plusDays(2);
    }
    
    /**
     * 创建默认到期日
     */
    public static LocalDate defaultMaturityDate() {
        return LocalDate.now().plusMonths(1);
    }
    
    /**
     * 创建默认时间戳
     */
    public static LocalDateTime defaultTimestamp() {
        return LocalDateTime.now();
    }
}
