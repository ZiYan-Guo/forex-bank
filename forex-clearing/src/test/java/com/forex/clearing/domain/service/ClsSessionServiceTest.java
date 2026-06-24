package com.forex.clearing.domain.service;

import com.forex.clearing.domain.model.aggregate.ClsSession;
import com.forex.clearing.domain.repository.ClsSessionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClsSessionServiceTest {

    @Mock private ClsSessionRepository sessionRepository;

    private ClsSessionService clsSessionService;

    @BeforeEach
    void setUp() {
        clsSessionService = new ClsSessionService(sessionRepository);
    }

    @Test
    @DisplayName("Schedule session creates and saves")
    void testScheduleSession() {
        when(sessionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ClsSession result = clsSessionService.scheduleSession(LocalDate.now());

        assertNotNull(result);
        assertTrue(result.getSessionId().startsWith("CLS"));
        verify(sessionRepository).save(any());
    }

    @Test
    @DisplayName("Open pay-in window transitions SCHEDULED→PAY_IN_OPEN")
    void testOpenPayInWindow() {
        ClsSession session = ClsSession.create("CLS001", LocalDate.now());
        when(sessionRepository.findBySessionId("CLS001")).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenReturn(session);

        clsSessionService.openPayInWindow("CLS001");

        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("Complete settlement saves final status")
    void testCompleteSettlement() {
        ClsSession session = ClsSession.create("CLS002", LocalDate.now());
        session.openPayIn();
        session.closePayIn();
        session.calculateNetPositions();
        when(sessionRepository.findBySessionId("CLS002")).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenReturn(session);

        clsSessionService.completeSettlement("CLS002");

        verify(sessionRepository).save(session);
    }
}
