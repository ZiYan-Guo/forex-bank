package com.forex.clearing.domain.service;

import com.forex.clearing.domain.model.aggregate.ClsSession;
import com.forex.clearing.domain.repository.ClsSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClsSessionService {

    private final ClsSessionRepository sessionRepository;

    public ClsSession scheduleSession(LocalDate date) {
        String sessionId = "CLS" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        ClsSession session = ClsSession.create(sessionId, date);
        ClsSession saved = sessionRepository.save(session);
        log.info("CLS session scheduled: sessionId={}, date={}", sessionId, date);
        return saved;
    }

    public void openPayInWindow(String sessionId) {
        ClsSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "CLS session not found"));
        session.openPayIn();
        sessionRepository.save(session);
        log.info("Pay-In window opened: sessionId={}", sessionId);
    }

    public void closePayInWindow(String sessionId) {
        ClsSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "CLS session not found"));
        session.closePayIn();
        sessionRepository.save(session);
        log.info("Pay-In window closed: sessionId={}", sessionId);
    }

    public void calculateNet(String sessionId) {
        ClsSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "CLS session not found"));
        session.calculateNetPositions();
        sessionRepository.save(session);
        log.info("Net positions calculated: sessionId={}", sessionId);
    }

    public void completeSettlement(String sessionId) {
        ClsSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "CLS session not found"));
        session.completeSettlement();
        sessionRepository.save(session);
        log.info("CLS settlement completed: sessionId={}", sessionId);
    }
}
