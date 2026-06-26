package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CIPS.112报文生成请求")
public class Cips112Req {

    @Schema(description = "原始报文ID")
    private String originalMsgId;

    @Schema(description = "状态码")
    private String status;

    @Schema(description = "原因码")
    private String reason;
}
