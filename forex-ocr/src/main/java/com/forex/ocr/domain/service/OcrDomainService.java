package com.forex.ocr.domain.service;

import com.forex.ocr.domain.event.OcrCompletedEvent;
import com.forex.ocr.domain.model.aggregate.OcrTask;
import com.forex.ocr.domain.repository.OcrTaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrDomainService {

    private final OcrTaskRepository ocrTaskRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OcrTask uploadDocument(String docType, String fileName, String filePath) {
        OcrTask task = OcrTask.create(docType, fileName, filePath);
        OcrTask saved = ocrTaskRepository.save(task);
        log.info("OCR单据上传成功: taskId={}, docType={}, fileName={}", saved.getTaskId(), docType, fileName);
        return saved;
    }

    public OcrTask processOcr(Long taskId) {
        OcrTask task = ocrTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("OCR任务不存在: " + taskId));

        if (!"UPLOADED".equals(task.getStatus())) {
            throw new IllegalStateException("OCR任务状态不允许处理: " + task.getStatus());
        }

        task.startProcessing();
        ocrTaskRepository.save(task);

        try {
            String result = simulateOcr(task);
            task.complete(result);
            ocrTaskRepository.save(task);

            eventPublisher.publishEvent(new OcrCompletedEvent(task.getId(), task.getDocType()));
            log.info("OCR识别完成: taskId={}, docType={}", task.getTaskId(), task.getDocType());
        } catch (Exception e) {
            task.fail(e.getMessage());
            ocrTaskRepository.save(task);
            log.error("OCR识别失败: taskId={}", task.getTaskId(), e);
        }

        return task;
    }

    public OcrTask getResult(Long taskId) {
        return ocrTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("OCR任务不存在: " + taskId));
    }

    private String simulateOcr(OcrTask task) {
        log.info("模拟OCR识别: taskId={}, docType={}, fileName={}", task.getTaskId(), task.getDocType(), task.getFileName());
        return String.format("{\"taskId\":\"%s\",\"docType\":\"%s\",\"fields\":{\"amount\":\"10000.00\",\"date\":\"2024-01-01\",\"currency\":\"USD\"}}",
                task.getTaskId(), task.getDocType());
    }
}
