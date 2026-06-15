package com.forex.exchange.application.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
/**
 * Enterprise multi-tier quote service. // 企业多级报价服务。
 * Provides customized quotes based on customer volume, credit tier, and
 * contribution history. // 根据客户的交易量、信用等级和贡献历史提供定制化报价。
 */
public class EnterpriseQuoteService {

    /**
     * Get enterprise-customized quote based on volume, credit tier, contribution
     * history. // 根据交易量、信用等级、贡献历史获取企业定制报价。
     */
    public Map<String, Object> getEnterpriseQuote(Long customerId, String currencyPair, BigDecimal amount,
            String dealType) {
        log.info("Calculating enterprise quote: customerId={}, pair={}, amount={}, dealType={}",
                customerId, currencyPair, amount, dealType);

        // Mock: 3 tiers - Standard, Premium, VIP // 模拟三个等级：标准、高级、VIP
        double baseRate = 7.2536;
        List<Map<String, Object>> tiers = List.of(
                Map.of("tier", "STANDARD", "rate", baseRate + 0.0010, "spread", 10,
                        "annualVolume", "0-1M"), // 年度交易量 0-100万
                Map.of("tier", "PREMIUM", "rate", baseRate + 0.0005, "spread", 5,
                        "annualVolume", "1M-10M"), // 年度交易量 100万-1000万
                Map.of("tier", "VIP", "rate", baseRate + 0.0002, "spread", 2,
                        "annualVolume", "10M+")); // 年度交易量 1000万+

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customerId", customerId);
        result.put("currencyPair", currencyPair);
        result.put("dealType", dealType);
        result.put("recommendedTier", "PREMIUM"); // 推荐等级：高级
        result.put("availableTiers", tiers); // 可用的报价等级列表
        log.info("Enterprise quote calculated: recommended tier=PREMIUM");
        return result;
    }
}
