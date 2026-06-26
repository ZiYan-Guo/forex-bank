package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "AuditCompareReq")
public class AuditCompareReq {

    @Schema(description = "invoiceId")
    private String invoiceId;

    @Schema(description = "billId")
    private String billId;

    @Schema(description = "customsId")
    private String customsId;

}
