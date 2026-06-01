package com.forex.notification.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Notification extends BaseAggregate {

    private Long id;
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

    private Notification() {
        super();
    }

    public static Notification create(String title, String content, String notifyType,
                                       String targetUsers, String targetUserNames,
                                       String bizType, String bizNo) {
        Notification notification = new Notification();
        notification.title = title;
        notification.content = content;
        notification.notifyType = notifyType;
        notification.targetUsers = targetUsers;
        notification.targetUserNames = targetUserNames;
        notification.bizType = bizType;
        notification.bizNo = bizNo;
        notification.status = "PENDING";
        notification.validate();
        return notification;
    }

    public static Notification reconstitute(Long id, String title, String content,
                                             String notifyType, String targetUsers,
                                             String targetUserNames, String bizType,
                                             String bizNo, String status,
                                             LocalDateTime sendTime, String failedReason) {
        Notification notification = new Notification();
        notification.id = id;
        notification.title = title;
        notification.content = content;
        notification.notifyType = notifyType;
        notification.targetUsers = targetUsers;
        notification.targetUserNames = targetUserNames;
        notification.bizType = bizType;
        notification.bizNo = bizNo;
        notification.status = status;
        notification.sendTime = sendTime;
        notification.failedReason = failedReason;
        return notification;
    }

    public void send() {
        if ("SENT".equals(this.status)) {
            throw new IllegalStateException("通知已发送");
        }
        this.status = "SENT";
        this.sendTime = LocalDateTime.now();
        markUpdated();
    }

    public void markSent() {
        this.status = "SENT";
        this.sendTime = LocalDateTime.now();
        markUpdated();
    }

    public void markFailed(String reason) {
        this.status = "FAILED";
        this.failedReason = reason;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("通知标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("通知内容不能为空");
        }
        if (notifyType == null || notifyType.isBlank()) {
            throw new IllegalArgumentException("通知类型不能为空");
        }
    }
}
