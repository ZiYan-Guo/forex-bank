package com.forex.ocr.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.ocr.application.query.OcrQuery;
import com.forex.ocr.domain.model.aggregate.OcrTask;
import com.forex.ocr.domain.repository.OcrTaskRepository;
import com.forex.ocr.domain.service.OcrDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OcrAppService {

    private final OcrDomainService ocrDomainService;
    private final OcrTaskRepository ocrTaskRepository;

    public OcrTask uploadDocument(String docType, String fileName, String filePath) {
        return ocrDomainService.uploadDocument(docType, fileName, filePath);
    }

    @Transactional
    public OcrTask processOcr(String taskId) {
        OcrTask task = ocrTaskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("OCR任务不存在: " + taskId));
        return ocrDomainService.processOcr(task.getId());
    }

    public OcrTask getOcrResult(String taskId) {
        OcrTask task = ocrTaskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("OCR任务不存在: " + taskId));
        return ocrDomainService.getResult(task.getId());
    }

    public PageResp<OcrTask> pageQuery(OcrQuery query) {
        return ocrTaskRepository.pageQuery(query);
    }
}
