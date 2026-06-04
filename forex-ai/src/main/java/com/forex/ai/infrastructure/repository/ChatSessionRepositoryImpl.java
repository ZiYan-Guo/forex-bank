package com.forex.ai.infrastructure.repository;

import com.forex.ai.domain.model.aggregate.ChatSession;
import com.forex.ai.domain.repository.ChatSessionRepository;
import com.forex.ai.infrastructure.mapper.ChatSessionMapper;
import com.forex.ai.infrastructure.persistence.ChatSessionPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatSessionRepositoryImpl implements ChatSessionRepository {

    private final ChatSessionMapper chatSessionMapper;

    @Override
    public Optional<ChatSession> findBySessionId(String sessionId) {
        ChatSessionPO po = chatSessionMapper.findBySessionId(sessionId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<ChatSession> findByUserId(String userId) {
        return chatSessionMapper.findByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void save(ChatSession session) {
        ChatSessionPO po = toPO(session);
        if (po.getId() == null) {
            chatSessionMapper.insert(po);
        } else {
            chatSessionMapper.updateById(po);
        }
    }

    private ChatSession toDomain(ChatSessionPO po) {
        return ChatSession.reconstitute(
                po.getId(), po.getSessionId(), po.getUserId(), po.getUserName(),
                po.getSessionType(), po.getTitle(), po.getStatus(),
                po.getCreateTime(), po.getUpdateTime(), po.getVersion());
    }

    private ChatSessionPO toPO(ChatSession session) {
        ChatSessionPO po = new ChatSessionPO();
        po.setId(session.getId());
        po.setSessionId(session.getSessionId());
        po.setUserId(session.getUserId());
        po.setUserName(session.getUserName());
        po.setSessionType(session.getSessionType());
        po.setTitle(session.getTitle());
        po.setStatus(session.getStatus());
        return po;
    }
}
