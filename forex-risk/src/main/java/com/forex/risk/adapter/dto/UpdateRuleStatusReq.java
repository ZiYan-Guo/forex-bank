package com.forex.risk.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Update sampling rule status request / 更新抽查规则状态请求")
public class UpdateRuleStatusReq {

    @Schema(description = "Status: ACTIVE/INACTIVE / 状态")
    private String status;
}
