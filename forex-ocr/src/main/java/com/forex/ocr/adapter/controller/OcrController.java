package com.forex.ocr.adapter.controller;

import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.ocr.adapter.dto.OcrTaskResp;
import com.forex.ocr.adapter.dto.OcrUploadReq;
import com.forex.ocr.application.query.OcrQuery;
import com.forex.ocr.application.service.OcrAppService;
import com.forex.ocr.domain.model.aggregate.OcrTask;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "OCR识别")
@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrAppService ocrAppService;

    @Operation(summary = "上传单据")
    @RequirePermission("ocr:upload")
    @PostMapping("/upload")
    public R<OcrTaskResp> upload(@RequestParam("docType") String docType,
                                  @RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        OcrTask task = ocrAppService.uploadDocument(docType, fileName, "/tmp/ocr/" + fileName);
        return R.ok("上传成功", toResp(task));
    }

    @Operation(summary = "处理OCR识别")
    @RequirePermission("ocr:process")
    @PostMapping("/process/{taskId}")
    public R<OcrTaskResp> process(@PathVariable String taskId) {
        OcrTask task = ocrAppService.processOcr(taskId);
        return R.ok("处理完成", toResp(task));
    }

    @Operation(summary = "查询OCR结果")
    @GetMapping("/result/{taskId}")
    public R<OcrTaskResp> getResult(@PathVariable String taskId) {
        OcrTask task = ocrAppService.getOcrResult(taskId);
        return R.ok(toResp(task));
    }

    @Operation(summary = "分页查询OCR任务")
    @RequirePermission("ocr:page")
    @PostMapping("/page")
    public R<PageResp<OcrTaskResp>> pageQuery(@RequestBody OcrQuery query) {
        PageResp<OcrTask> pageResp = ocrAppService.pageQuery(query);
        List<OcrTaskResp> records = pageResp.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<OcrTaskResp> resp = PageResp.of(
                pageResp.getTotal(), records, pageResp.getPageNum(), pageResp.getPageSize());
        return R.ok(resp);
    }

    private OcrTaskResp toResp(OcrTask task) {
        OcrTaskResp resp = new OcrTaskResp();
        resp.setId(task.getId());
        resp.setTaskId(task.getTaskId());
        resp.setDocType(task.getDocType());
        resp.setFileName(task.getFileName());
        resp.setFilePath(task.getFilePath());
        resp.setOcrResult(task.getOcrResult());
        resp.setRecognizedFields(task.getRecognizedFields());
        resp.setStatus(task.getStatus());
        resp.setErrorMsg(task.getErrorMsg());
        resp.setUploadTime(task.getUploadTime());
        resp.setCompleteTime(task.getCompleteTime());
        return resp;
    }
}
