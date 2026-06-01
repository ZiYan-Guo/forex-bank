package com.forex.notification.application.query;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeQuery extends PageReq {

    @Schema(description = "公告类型")
    private String noticeType;

    @Schema(description = "发布状态")
    private String publishStatus;
}
