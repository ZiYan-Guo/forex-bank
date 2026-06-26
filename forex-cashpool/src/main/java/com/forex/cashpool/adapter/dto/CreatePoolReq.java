package com.forex.cashpool.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建资金池请求")
public class CreatePoolReq {
    @Schema(description = "资金池名称") private String poolName;
    @Schema(description = "币种") private String currency;
}
