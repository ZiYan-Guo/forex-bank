package com.forex.notification.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateNoticeReq {

    @NotBlank(message = "公告标题不能为空")
    @Schema(description = "公告标题", example = "系统维护通知")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Schema(description = "公告内容", example = "系统将于周六凌晨进行维护")
    private String content;

    @NotBlank(message = "公告类型不能为空")
    @Schema(description = "公告类型", example = "ANNOUNCEMENT")
    private String noticeType;

    @Schema(description = "发布人ID")
    private Long publisherId;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
}
