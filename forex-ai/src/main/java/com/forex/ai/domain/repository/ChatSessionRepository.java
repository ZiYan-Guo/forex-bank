package com.forex.ai.domain.repository;

import com.forex.ai.domain.model.aggregate.ChatSession;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository {

    Optional<ChatSession> findBySessionId(String sessionId);

    List<ChatSession> findByUserId(String userId);

    void save(ChatSession session);
}
