package com.forex.ai.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.ai.infrastructure.persistence.ChatSessionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatSessionMapper extends BaseMapperExt<ChatSessionPO> {

    @Select("SELECT * FROM t_chat_session WHERE session_id = #{sessionId} AND deleted = 0")
    ChatSessionPO findBySessionId(@Param("sessionId") String sessionId);

    @Select("SELECT * FROM t_chat_session WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<ChatSessionPO> findByUserId(@Param("userId") String userId);
}
