package com.forex.clearing.application.service;

import com.forex.clearing.domain.model.aggregate.SettlementTracker;
import com.forex.clearing.domain.repository.SettlementTrackerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementTrackerService {

    private final SettlementTrackerRepository trackerRepository;

    public SettlementTracker createTracker(String paymentNo, String instructionNo, String channel) {
        String trackingId = "TRK" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        SettlementTracker tracker = SettlementTracker.create(trackingId, paymentNo, instructionNo, "PENDING_SEND", channel);
        return trackerRepository.save(tracker);
    }

    public void updateStatus(String trackingId, String newStatus) {
        SettlementTracker tracker = trackerRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Tracker not found"));
        tracker.transitionTo(newStatus);
        trackerRepository.save(tracker);
    }

    public void markException(String trackingId, String reason, String detail) {
        SettlementTracker tracker = trackerRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Tracker not found"));
        tracker.markException(reason, detail);
        trackerRepository.save(tracker);
    }

    public List<SettlementTracker> findOverdue() {
        return trackerRepository.findAll().stream()
                .filter(SettlementTracker::isOverdue)
                .toList();
    }
}
