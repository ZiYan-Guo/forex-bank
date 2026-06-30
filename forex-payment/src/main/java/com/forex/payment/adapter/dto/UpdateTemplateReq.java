package com.forex.payment.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "UpdateTemplateReq")
public class UpdateTemplateReq {
    @Schema(description = "templateName") private String templateName;
    @Schema(description = "scenarioType") private String scenarioType;
    @Schema(description = "paymentDirection") private String paymentDirection;
    @Schema(description = "defaultPayCurrency") private String defaultPayCurrency;
    @Schema(description = "defaultBeneficiaryCountry") private String defaultBeneficiaryCountry;
    @Schema(description = "beneficiaryDetails") private String beneficiaryDetails;
    @Schema(description = "defaultPurpose") private String defaultPurpose;
    @Schema(description = "defaultPurposeCode") private String defaultPurposeCode;
    @Schema(description = "usageInstructions") private String usageInstructions;
    @Schema(description = "sortOrder") private Integer sortOrder;
    @Schema(description = "isPublic") private Boolean isPublic;
    @Schema(description = "ownerCustomerId") private Long ownerCustomerId;
}
