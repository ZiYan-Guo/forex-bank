package com.forex.clearing.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "对账SWIFT预览请求")
public class ReconcilePreviewReq {
    @Schema(description = "交易编号") private String tradeNo;
    @Schema(description = "SWIFT报文") private String swiftMessage;
    @Schema(description = "日期") private String date;
    @Schema(description = "阈值") private String threshold;
}
