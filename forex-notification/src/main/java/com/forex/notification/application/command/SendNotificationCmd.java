package com.forex.notification.application.command;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendNotificationCmd {

    @NotBlank(message = "通知标题不能为空")
    private String title;

    @NotBlank(message = "通知内容不能为空")
    private String content;

    @NotBlank(message = "通知类型不能为空")
    private String notifyType;

    private String targetUsers;

    private String targetUserNames;

    private String bizType;

    private String bizNo;
}
