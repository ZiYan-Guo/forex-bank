package com.forex.schedule.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Schedule job log response / 任务日志响应")
public class JobLogResp {

    @Schema(description = "Primary key / 主键ID")
    private Long id;

    @Schema(description = "Job ID / 任务ID")
    private Long jobId;

    @Schema(description = "Job name / 任务名称")
    private String jobName;

    @Schema(description = "Job handler / 任务处理器")
    private String jobHandler;

    @Schema(description = "Start time / 开始时间")
    private LocalDateTime startTime;

    @Schema(description = "End time / 结束时间")
    private LocalDateTime endTime;

    @Schema(description = "Execution status: RUNNING/SUCCESS/FAILED / 执行状态")
    private String executeStatus;

    @Schema(description = "Execution result / 执行结果")
    private String executeResult;

    @Schema(description = "Error message / 错误信息")
    private String errorMsg;
}
