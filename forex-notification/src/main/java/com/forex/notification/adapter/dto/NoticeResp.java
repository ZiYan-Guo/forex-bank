package com.forex.notification.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeResp {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "公告类型")
    private String noticeType;

    @Schema(description = "发布状态")
    private String publishStatus;

    @Schema(description = "发布人ID")
    private Long publisherId;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
}
