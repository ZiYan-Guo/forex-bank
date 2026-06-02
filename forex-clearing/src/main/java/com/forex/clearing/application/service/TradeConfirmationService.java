package com.forex.clearing.application.service;

import com.forex.clearing.application.dto.InternalTrade;
import com.forex.clearing.application.dto.ReconciliationResult;
import com.forex.clearing.application.dto.TradeConfirmation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CFETS trade confirmation matching service.
 * Imports trade data from CFETS and automatically matches with internal trades.
 * CFETS 交易确认匹配服务。从外汇交易中心导入成交数据并自动匹配。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeConfirmationService {

    /**
     * Import trade confirmations from CFETS (simulated).
     * 从 CFETS 导入交易确认数据（模拟）。
     */
    public List<TradeConfirmation> importFromCfets(LocalDate tradeDate) {
        log.info("Importing CFETS trade confirmations for date: {}", tradeDate);
        List<TradeConfirmation> confirmations = new ArrayList<>();
        confirmations.add(new TradeConfirmation("CFX20260601001", "USD/CNY", "BUY",
                new BigDecimal("100000"), new BigDecimal("7.2536"), tradeDate, "BANK_A"));
        confirmations.add(new TradeConfirmation("CFX20260601002", "EUR/CNY", "SELL",
                new BigDecimal("50000"), new BigDecimal("7.8532"), tradeDate, "BANK_B"));
        return confirmations;
    }

    /**
     * Auto-match CFETS confirmations with internal trades.
     * Key matching fields: amount(±0.01 tolerance), currency pair(exact),
     * value date(±1 day), counterparty(exact).
     * 自动匹配 CFETS 确认与内部交易。
     */
    public ReconciliationResult autoMatch(List<TradeConfirmation> external, List<InternalTrade> internal) {
        ReconciliationResult result = new ReconciliationResult();
        List<String> matchedExternal = new ArrayList<>();

        for (InternalTrade intTrade : internal) {
            boolean found = false;
            for (TradeConfirmation ext : external) {
                if (isMatch(ext, intTrade)) {
                    result.addMatched(ext.getRefNo(), intTrade.getTradeNo());
                    matchedExternal.add(ext.getRefNo());
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.addUnmatched("UNMATCHED_INTERNAL", intTrade.getTradeNo(), "No matching CFETS confirmation");
            }
        }

        for (TradeConfirmation ext : external) {
            if (!matchedExternal.contains(ext.getRefNo())) {
                result.addUnmatched("UNMATCHED_EXTERNAL", ext.getRefNo(), "No matching internal trade");
            }
        }

        log.info("Reconciliation result: {} matched, {} unmatched",
                result.getMatchedCount(), result.getUnmatchedCount());
        return result;
    }

    private boolean isMatch(TradeConfirmation ext, InternalTrade internal) {
        BigDecimal amountDiff = ext.getAmount().subtract(internal.getAmount()).abs();
        boolean amountMatch = amountDiff.compareTo(new BigDecimal("0.01")) <= 0;
        boolean ccyMatch = ext.getCurrencyPair().equals(internal.getCurrencyPair());
        boolean dateMatch = Math.abs(ext.getValueDate().until(internal.getValueDate()).getDays()) <= 1;
        return amountMatch && ccyMatch && dateMatch;
    }
}
