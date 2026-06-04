package com.forex.ai.domain.repository;

import com.forex.ai.domain.model.aggregate.ChatMessage;

import java.util.List;

public interface ChatMessageRepository {

    List<ChatMessage> findBySessionId(String sessionId);

    void save(ChatMessage message);
}
