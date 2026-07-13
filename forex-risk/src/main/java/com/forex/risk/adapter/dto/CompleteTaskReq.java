package com.forex.risk.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Complete sampling task request / 完成抽查任务请求")
public class CompleteTaskReq {
    @Schema(description = "Review result / 检查结果")
    private String result;

    @Schema(description = "Review comment / 检查意见")
    private String comment;
}
