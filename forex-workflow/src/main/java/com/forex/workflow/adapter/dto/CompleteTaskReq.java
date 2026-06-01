package com.forex.workflow.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CompleteTaskReq {

    @NotBlank(message = "审批结果不能为空")
    @Schema(description = "审批结果", example = "APPROVED")
    private String approveResult;

    @Schema(description = "审批意见")
    private String comment;
}
