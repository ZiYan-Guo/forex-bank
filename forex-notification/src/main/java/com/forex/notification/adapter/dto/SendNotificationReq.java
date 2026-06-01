package com.forex.notification.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendNotificationReq {

    @NotBlank(message = "通知标题不能为空")
    @Schema(description = "通知标题", example = "交易确认通知")
    private String title;

    @NotBlank(message = "通知内容不能为空")
    @Schema(description = "通知内容", example = "您的结售汇交易已确认")
    private String content;

    @NotBlank(message = "通知类型不能为空")
    @Schema(description = "通知类型", example = "SMS")
    private String notifyType;

    @Schema(description = "目标用户ID列表(JSON数组)")
    private String targetUsers;

    @Schema(description = "目标用户名称列表")
    private String targetUserNames;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务编号")
    private String bizNo;
}
