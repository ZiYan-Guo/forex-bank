package com.forex.schedule.application.query;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JobQuery extends PageReq {

    @Schema(description = "Job name / 任务名称")
    private String jobName;

    @Schema(description = "Job group / 任务组")
    private String jobGroup;

    @Schema(description = "Status: ENABLED/DISABLED / 状态")
    private String status;
}
