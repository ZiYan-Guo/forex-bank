package com.forex.risk.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Generate sampling tasks request / 生成抽查任务请求")
public class GenerateSamplingReq {
    @Schema(description = "Business date, yyyy-MM-dd / 业务日期")
    private String date;
}
