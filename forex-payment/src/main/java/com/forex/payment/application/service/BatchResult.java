package com.forex.payment.application.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchResult {
    List<String> successNos = new ArrayList<>();
    List<FailedItem> failures = new ArrayList<>();

    public void addSuccess(String no) {
        successNos.add(no);
    }

    public void addFailure(String ref, String error) {
        failures.add(new FailedItem(ref, error));
    }

    public int getSuccessCount() {
        return successNos.size();
    }

    public int getFailureCount() {
        return failures.size();
    }

    @Data
    @AllArgsConstructor
    public static class FailedItem {
        String reference;
        String error;
    }
}
