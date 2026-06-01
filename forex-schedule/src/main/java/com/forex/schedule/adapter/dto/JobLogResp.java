package com.forex.schedule.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "任务日志响应")
public class JobLogResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务ID")
    private Long jobId;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务处理器")
    private String jobHandler;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "执行状态: RUNNING/SUCCESS/FAILED")
    private String executeStatus;

    @Schema(description = "执行结果")
    private String executeResult;

    @Schema(description = "错误信息")
    private String errorMsg;
}
