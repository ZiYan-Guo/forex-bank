package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "单据审计响应")
public class DocumentAuditResp {

    @Schema(description = "审计ID")
    private String auditId;

    @Schema(description = "是否一致")
    private Boolean isConsistent;

    @Schema(description = "差异列表")
    private List<String> discrepancies;

    @Schema(description = "审计意见")
    private String opinion;
}
