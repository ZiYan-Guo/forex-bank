package com.forex.ocr.domain.service;

import com.forex.ocr.domain.event.OcrCompletedEvent;
import com.forex.ocr.domain.model.aggregate.OcrTask;
import com.forex.ocr.domain.repository.OcrTaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcrDomainServiceTest {

    @Mock private OcrTaskRepository ocrTaskRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OcrDomainService ocrDomainService;

    @Test
    @DisplayName("Upload document creates OCR task")
    void testUploadDocument() {
        OcrTask task = mock(OcrTask.class);
        when(ocrTaskRepository.save(any())).thenReturn(task);

        OcrTask result = ocrDomainService.uploadDocument("INVOICE", "test.pdf", "/tmp/test.pdf");
        assertNotNull(result);
        verify(ocrTaskRepository).save(any());
    }

    @Test
    @DisplayName("Process OCR completes and publishes event")
    void testProcessOcr() {
        OcrTask task = mock(OcrTask.class);
        when(task.getStatus()).thenReturn("UPLOADED");
        when(task.getDocType()).thenReturn("INVOICE");
        when(task.getFileName()).thenReturn("test.pdf");
        when(task.getTaskId()).thenReturn("OCR001");
        when(ocrTaskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(ocrTaskRepository.save(any())).thenReturn(task);

        OcrTask result = ocrDomainService.processOcr(1L);

        verify(task).startProcessing();
        verify(task).complete(anyString());
        verify(eventPublisher).publishEvent(any(OcrCompletedEvent.class));
    }

    @Test
    @DisplayName("Process OCR with wrong status throws")
    void testProcessOcr_WrongStatus() {
        OcrTask task = mock(OcrTask.class);
        when(task.getStatus()).thenReturn("COMPLETED");
        when(ocrTaskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(IllegalStateException.class, () -> ocrDomainService.processOcr(1L));
    }

    @Test
    @DisplayName("Get result returns existing task")
    void testGetResult() {
        OcrTask task = mock(OcrTask.class);
        when(ocrTaskRepository.findById(1L)).thenReturn(Optional.of(task));

        OcrTask result = ocrDomainService.getResult(1L);
        assertNotNull(result);
    }
}
