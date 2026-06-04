package com.forex.ai.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "AI对话请求")
public class ChatMessageReq {

    @Schema(description = "会话ID")
    private String sessionId;

    @NotBlank(message = "问题不能为空")
    @Schema(description = "问题内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String question;

    @Schema(description = "是否流式输出")
    private Boolean stream;
}
