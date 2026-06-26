package com.forex.ai.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class ChatSession extends BaseAggregate {

    private static final long serialVersionUID = 1L;

    public static final String SUPPORT = "SUPPORT";
    public static final String TRADING = "TRADING";
    public static final String AUDIT = "AUDIT";

    public static final String ACTIVE = "ACTIVE";
    public static final String CLOSED = "CLOSED";

    private Long id;
    private String sessionId;
    private String userId;
    private String userName;
    private String sessionType;
    private String title;
    private String status;
    private transient List<ChatMessage> messages;

    private ChatSession() {
        super();
        this.messages = new ArrayList<>();
    }

    public static ChatSession create(String userId, String userName, String sessionType, String title) {
        ChatSession session = new ChatSession();
        session.sessionId = "SES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        session.userId = userId;
        session.userName = userName;
        session.sessionType = sessionType;
        session.title = title;
        session.status = ACTIVE;
        session.messages = new ArrayList<>();
        session.validate();
        return session;
    }

    public static ChatSession reconstitute(Long id, String sessionId, String userId, String userName,
                                            String sessionType, String title, String status,
                                            LocalDateTime createdAt, LocalDateTime updatedAt,
                                            Integer version) {
        ChatSession session = new ChatSession();
        session.id = id;
        session.sessionId = sessionId;
        session.userId = userId;
        session.userName = userName;
        session.sessionType = sessionType;
        session.title = title;
        session.status = status;
        session.messages = new ArrayList<>();
        return session;
    }

    public void addMessage(ChatMessage msg) {
        if (msg == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "message must not be null");
        }
        if (CLOSED.equals(this.status)) {
            throw new IllegalStateException("Cannot add message to a closed session");
        }
        this.messages.add(msg);
        markUpdated();
    }

    public void close() {
        if (CLOSED.equals(this.status)) {
            throw new IllegalStateException("Session is already closed");
        }
        this.status = CLOSED;
        markUpdated();
    }

    public int getMessageCount() {
        return messages != null ? messages.size() : 0;
    }

    public void loadMessages(List<ChatMessage> messages) {
        this.messages = messages != null ? new ArrayList<>(messages) : new ArrayList<>();
    }

    @Override
    protected void validate() {
        if (sessionType == null || sessionType.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "sessionType must not be blank");
        }
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "userId must not be blank");
        }
    }
}
