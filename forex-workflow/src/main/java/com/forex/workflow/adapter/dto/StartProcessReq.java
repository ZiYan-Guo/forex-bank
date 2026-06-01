package com.forex.workflow.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class StartProcessReq {

    @NotBlank(message = "业务类型不能为空")
    @Schema(description = "业务类型", example = "PAYMENT_APPROVAL")
    private String bizType;

    @NotBlank(message = "业务编号不能为空")
    @Schema(description = "业务编号", example = "PY202401010001")
    private String bizNo;

    @NotBlank(message = "任务标题不能为空")
    @Schema(description = "任务标题", example = "跨境汇款审批")
    private String title;

    @Schema(description = "受理人")
    private String assignee;

    @Schema(description = "受理人名称")
    private String assigneeName;

    @Schema(description = "流程变量JSON")
    private String variables;
}
