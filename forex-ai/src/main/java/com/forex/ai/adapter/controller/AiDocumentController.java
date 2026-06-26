package com.forex.ai.adapter.controller;

import com.forex.ai.application.service.AiAppService;
import com.forex.common.base.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.ai.adapter.dto.ClearingCorrectionReq;
import com.forex.ai.adapter.dto.AuditCompareReq;
import com.forex.ai.adapter.dto.OcrUploadReq;

@Tag(name = "AI智能审单")
@RestController
@RequestMapping("/api/ai/document")
@RequiredArgsConstructor
public class AiDocumentController {

    private final AiAppService aiAppService;

    @Operation(summary = "单据上传识别")
    @RequirePermission("ai:upload")
    @PostMapping("/ocr/upload")
    public R<Map<String, Object>> ocrUpload(@RequestBody Map<String, Object> req) {
        String docType = (String) req.getOrDefault("docType", "INVOICE");
        String imageBase64 = (String) req.getOrDefault("imageBase64", "");
        return R.ok(aiAppService.ocrRecognize(docType, imageBase64));
    }

    @Operation(summary = "三单比对")
    @RequirePermission("ai:compare")
    @PostMapping("/audit/compare")
    public R<Map<String, Object>> auditCompare(@RequestBody Map<String, Object> req) {
        String invoiceId = (String) req.getOrDefault("invoiceId", "");
        String billId = (String) req.getOrDefault("billId", "");
        String customsId = (String) req.getOrDefault("customsId", "");
        return R.ok(aiAppService.compareDocuments(invoiceId, billId, customsId));
    }

    @Operation(summary = "清算差异修正")
    @RequirePermission("ai:correction")
    @PostMapping("/clearing/correction")
    public R<Map<String, Object>> clearingCorrection(@RequestBody Map<String, Object> req) {
        String clearingId = (String) req.getOrDefault("clearingId", "");
        return R.ok(aiAppService.clearingCorrection(clearingId));
    }
}
