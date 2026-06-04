package com.forex.ai.infrastructure.repository;

import com.forex.ai.domain.model.aggregate.ChatMessage;
import com.forex.ai.domain.repository.ChatMessageRepository;
import com.forex.ai.infrastructure.mapper.ChatMessageMapper;
import com.forex.ai.infrastructure.persistence.ChatMessagePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final ChatMessageMapper chatMessageMapper;

    @Override
    public List<ChatMessage> findBySessionId(String sessionId) {
        return chatMessageMapper.findBySessionId(sessionId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void save(ChatMessage message) {
        ChatMessagePO po = toPO(message);
        if (po.getId() == null) {
            chatMessageMapper.insert(po);
        } else {
            chatMessageMapper.updateById(po);
        }
    }

    private ChatMessage toDomain(ChatMessagePO po) {
        return ChatMessage.reconstitute(
                po.getSessionId() + "_" + po.getId(),
                po.getRole(), po.getContent(), po.getCreateTime());
    }

    private ChatMessagePO toPO(ChatMessage message) {
        ChatMessagePO po = new ChatMessagePO();
        po.setRole(message.getRole());
        po.setContent(message.getContent());
        po.setCreateTime(message.getTimestamp());
        return po;
    }
}
