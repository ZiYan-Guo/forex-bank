package com.forex.workflow.application.query;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkflowQuery extends PageReq {

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "受理人")
    private String assignee;
}
