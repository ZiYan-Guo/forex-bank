package com.forex.cashpool.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.transaction.annotation.Transactional;

/**
 * 额度计算引擎 - 实现外债额度、放款额度、使用率等核心计算逻辑
 * Quota Calculation Engine - Core calculation logic for debt limits, lending limits, and usage rates
 * 基于央行宏观审慎管理参数进行计算
 * Calculations based on PBOC macro-prudential management parameters
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuotaCalculationEngine {

    private static final BigDecimal MACRO_PRUDENTIAL_FACTOR_DEBT = new BigDecimal("3.5");
    private static final BigDecimal MACRO_PRUDENTIAL_FACTOR_LENDING = new BigDecimal("0.8");

    /**
     * 计算外债额度 - 根据SAFE公式，基于净资产乘以宏观审慎参数
     * Calculate cross-border foreign debt limit per SAFE formula
     * 公式：外债额度 = 净资产 × 宏观审慎参数(3.5)
     */
    public BigDecimal calculateDebtLimit(BigDecimal netAssets) {
        log.info("计算外债额度: netAssets={}", netAssets);
        if (netAssets == null || netAssets.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("净资产无效或非正数，外债额度返回0, netAssets={}", netAssets);
            return BigDecimal.ZERO;
        }
        BigDecimal limit = netAssets.multiply(MACRO_PRUDENTIAL_FACTOR_DEBT)
                .setScale(2, RoundingMode.HALF_UP);
        log.info("外债额度计算完成: {}", limit);
        return limit;
    }

    /**
     * 计算境外放款额度 - 基于净资产乘以放款宏观审慎参数
     * Calculate overseas lending limit
     * 公式：境外放款额度 = 净资产 × 宏观审慎参数(0.8)
     */
    public BigDecimal calculateLendingLimit(BigDecimal netAssets) {
        log.info("计算境外放款额度: netAssets={}", netAssets);
        if (netAssets == null || netAssets.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("净资产无效或非正数，放款额度返回0, netAssets={}", netAssets);
            return BigDecimal.ZERO;
        }
        BigDecimal limit = netAssets.multiply(MACRO_PRUDENTIAL_FACTOR_LENDING)
                .setScale(2, RoundingMode.HALF_UP);
        log.info("放款额度计算完成: {}", limit);
        return limit;
    }

    /**
     * 计算额度使用率 - 已使用额度 / 总额度 × 100
     * Calculate limit usage percentage
     * 公式：使用率 = (已使用额度 / 总额度) × 100
     */
    public BigDecimal calculateUsagePct(BigDecimal used, BigDecimal total) {
        log.info("计算额度使用率: used={}, total={}", used, total);
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("总额度为0或null，使用率返回0");
            return BigDecimal.ZERO;
        }
        BigDecimal usagePct = used.divide(total, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        log.info("额度使用率计算完成: {}%", usagePct);
        return usagePct;
    }
}
