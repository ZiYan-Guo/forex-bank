package com.forex.ocr.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Schema(description = "OCR上传请求")
public class OcrUploadReq {

    @Schema(description = "单据类型", example = "INVOICE")
    @NotBlank(message = "单据类型不能为空")
    private String docType;
}
