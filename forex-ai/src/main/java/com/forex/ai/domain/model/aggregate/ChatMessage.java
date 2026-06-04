package com.forex.ai.domain.model.aggregate;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_USER = "USER";
    public static final String ROLE_AI = "AI";
    public static final String ROLE_SYSTEM = "SYSTEM";

    private String messageId;
    private String role;
    private String content;
    private LocalDateTime timestamp;

    private ChatMessage() {
    }

    public static ChatMessage of(String role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.messageId = "MSG-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        msg.role = role;
        msg.content = content;
        msg.timestamp = LocalDateTime.now();
        return msg;
    }

    public static ChatMessage reconstitute(String messageId, String role, String content, LocalDateTime timestamp) {
        ChatMessage msg = new ChatMessage();
        msg.messageId = messageId;
        msg.role = role;
        msg.content = content;
        msg.timestamp = timestamp;
        return msg;
    }

    public boolean isUserMessage() {
        return ROLE_USER.equals(role);
    }

    public boolean isAiMessage() {
        return ROLE_AI.equals(role);
    }
}
