package com.forex.ai.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.forex.ai.infrastructure.persistence.ChatMessagePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessagePO> {

    @Select("SELECT * FROM t_chat_message WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<ChatMessagePO> findBySessionId(@Param("sessionId") String sessionId);
}
