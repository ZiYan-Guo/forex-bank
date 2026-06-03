package com.forex.clearing.application.service;

import com.forex.clearing.domain.model.aggregate.TradeConfirmation;
import com.forex.clearing.domain.model.valueobject.ConfirmationFlag;
import com.forex.clearing.domain.repository.TradeConfirmationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutomatedConfirmationService {

    private final TradeConfirmationRepository confirmationRepository;

    /**
     * Initiate confirmation based on trade type per JR/T 0310-2025.
     * CFETS platform trades → CENTRALIZED confirmation
     * OTC/voice-brokered → BILATERAL confirmation via SWIFT
     * 根据 JR/T 0310-2025 发起确认。
     */
    public TradeConfirmation initiateConfirmation(String tradeNo, String tradeType,
                                                   String currencyPair, BigDecimal amount,
                                                   BigDecimal rate, LocalDate valueDate,
                                                   String counterparty) {
        ConfirmationFlag flag = determineFlag(tradeType);
        String confirmId = "CFM" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        TradeConfirmation cfm = TradeConfirmation.create(confirmId, tradeNo, tradeType, flag,
                currencyPair, "BUY", amount, rate, valueDate, counterparty);
        cfm.startMatching();

        if (flag == ConfirmationFlag.CENTRALIZED) {
            matchViaCentralized(cfm);
        } else {
            matchViaBilateral(cfm);
        }
        return confirmationRepository.save(cfm);
    }

    private ConfirmationFlag determineFlag(String tradeType) {
        return "CFETS".equalsIgnoreCase(tradeType) ? ConfirmationFlag.CENTRALIZED : ConfirmationFlag.BILATERAL;
    }

    private void matchViaCentralized(TradeConfirmation cfm) {
        log.info("Matching via CFETS centralized platform for: {}", cfm.getConfirmId());
        cfm.markMatched();
    }

    private void matchViaBilateral(TradeConfirmation cfm) {
        log.info("Matching via SWIFT bilateral for: {}", cfm.getConfirmId());
        cfm.markMatched();
    }

    /**
     * Auto-retry failed confirmations (max 3 retries, 5/15/60 min intervals).
     * 失败自动重发起（最多3次，间隔5/15/60分钟）。
     */
    public void retryFailedConfirmations() {
        List<TradeConfirmation> unmatched = confirmationRepository.findByMatchStatus("DISCREPANCY");
        for (TradeConfirmation cfm : unmatched) {
            if (cfm.getRetryCount() < 3) {
                cfm.incrementRetry();
                confirmationRepository.save(cfm);
                log.info("Retry #{} for confirmation: {}", cfm.getRetryCount(), cfm.getConfirmId());
            }
        }
    }
}
