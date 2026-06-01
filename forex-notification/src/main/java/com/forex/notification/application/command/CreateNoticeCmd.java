package com.forex.notification.application.command;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateNoticeCmd {

    @NotBlank(message = "公告标题不能为空")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    @NotBlank(message = "公告类型不能为空")
    private String noticeType;

    private Long publisherId;

    private LocalDateTime expireTime;
}
