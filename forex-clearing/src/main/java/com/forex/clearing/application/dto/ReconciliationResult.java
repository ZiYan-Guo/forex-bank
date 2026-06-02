package com.forex.clearing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Reconciliation result containing matched and unmatched items.
 * 对账结果，包含已匹配和未匹配项。
 */
@Data
public class ReconciliationResult {

    private List<MatchedPair> matched = new ArrayList<>();
    private List<UnmatchedItem> unmatched = new ArrayList<>();

    public void addMatched(String externalRef, String internalRef) {
        matched.add(new MatchedPair(externalRef, internalRef));
    }

    public void addUnmatched(String type, String refNo, String reason) {
        unmatched.add(new UnmatchedItem(type, refNo, reason));
    }

    public int getMatchedCount() {
        return matched.size();
    }

    public int getUnmatchedCount() {
        return unmatched.size();
    }

    @Data
    @AllArgsConstructor
    public static class MatchedPair {
        private String externalRef;
        private String internalRef;
    }

    @Data
    @AllArgsConstructor
    public static class UnmatchedItem {
        private String type;
        private String refNo;
        private String reason;
    }
}
