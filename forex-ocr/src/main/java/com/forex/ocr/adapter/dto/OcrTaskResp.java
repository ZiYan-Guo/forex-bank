package com.forex.ocr.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "OCR任务响应")
public class OcrTaskResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "单据类型: INVOICE/CONTRACT/LC_DOC/ID_CARD/BILL_OF_LADING")
    private String docType;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "OCR识别结果(JSON)")
    private String ocrResult;

    @Schema(description = "识别字段(JSON)")
    private String recognizedFields;

    @Schema(description = "状态: UPLOADED/PROCESSING/COMPLETED/FAILED")
    private String status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;
}
