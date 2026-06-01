package com.forex.notification.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_notification")
public class NotificationPO extends BasePO {

    private String title;
    private String content;
    private String notifyType;
    private String targetUsers;
    private String targetUserNames;
    private String bizType;
    private String bizNo;
    private String status;
    private LocalDateTime sendTime;
    private String failedReason;
}
