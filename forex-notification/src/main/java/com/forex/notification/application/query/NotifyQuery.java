package com.forex.notification.application.query;

import com.forex.common.base.dto.PageReq;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotifyQuery extends PageReq {

    @Schema(description = "通知类型")
    private String notifyType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "业务类型")
    private String bizType;
}
