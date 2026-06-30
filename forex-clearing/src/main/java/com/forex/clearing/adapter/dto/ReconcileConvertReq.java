package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报文转换请求")
public class ReconcileConvertReq {
    @Schema(description = "源报文类型") private String sourceType;
    @Schema(description = "源报文内容") private String sourceMessage;
    @Schema(description = "原始报文") private String raw;
}
