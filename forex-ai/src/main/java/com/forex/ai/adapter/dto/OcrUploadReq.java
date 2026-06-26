package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "OcrUploadReq")
public class OcrUploadReq {

    @Schema(description = "docType")
    private String docType;

    @Schema(description = "imageBase64")
    private String imageBase64;

}
