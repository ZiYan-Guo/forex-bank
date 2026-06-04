package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "AI对话响应")
public class ChatMessageResp {

    @Schema(description = "回答内容")
    private String answer;

    @Schema(description = "信息来源")
    private List<String> sources;

    @Schema(description = "置信度")
    private BigDecimal confidence;
}
