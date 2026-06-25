package com.forex.ai.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AiDomainService {

    private final RatePredictionEngine ratePredictionEngine;
    private final SmartRagService smartRagService;
    private final AmlDetectionEngine amlDetectionEngine;
    private final DocumentAuditService documentAuditService;
    private final TradingAssistant tradingAssistant;
}
