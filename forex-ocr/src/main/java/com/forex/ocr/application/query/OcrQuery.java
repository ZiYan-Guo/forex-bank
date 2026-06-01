package com.forex.ocr.application.query;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OcrQuery extends PageReq {

    @Schema(description = "单据类型: INVOICE/CONTRACT/LC_DOC/ID_CARD/BILL_OF_LADING")
    private String docType;

    @Schema(description = "状态: UPLOADED/PROCESSING/COMPLETED/FAILED")
    private String status;
}
