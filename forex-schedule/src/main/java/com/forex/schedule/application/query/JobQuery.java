package com.forex.schedule.application.query;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JobQuery extends PageReq {

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务组")
    private String jobGroup;

    @Schema(description = "状态: ENABLED/DISABLED")
    private String status;
}
