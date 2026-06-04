package com.forex.ai.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_chat_message")
public class ChatMessagePO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String sessionId;
    private String role;
    private String content;
    private String sources;
    private BigDecimal confidence;
    private LocalDateTime createTime;
}
