package com.forex.ai.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_chat_session")
public class ChatSessionPO extends BasePO {

    private String sessionId;
    private String userId;
    private String userName;
    private String sessionType;
    private String title;
    private String status;
}
