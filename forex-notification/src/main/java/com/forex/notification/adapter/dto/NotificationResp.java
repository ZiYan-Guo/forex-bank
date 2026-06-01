package com.forex.notification.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "通知类型")
    private String notifyType;

    @Schema(description = "目标用户ID列表")
    private String targetUsers;

    @Schema(description = "目标用户名称列表")
    private String targetUserNames;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务编号")
    private String bizNo;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "失败原因")
    private String failedReason;
}
