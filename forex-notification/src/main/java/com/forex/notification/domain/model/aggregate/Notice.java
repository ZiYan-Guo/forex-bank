package com.forex.notification.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

import java.time.LocalDateTime;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class Notice extends BaseAggregate {

    private Long id;
    private String title;
    private String content;
    private String noticeType;
    private String publishStatus;
    private Long publisherId;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;

    private Notice() {
        super();
    }

    public static Notice create(String title, String content, String noticeType,
                                 Long publisherId, LocalDateTime expireTime) {
        Notice notice = new Notice();
        notice.title = title;
        notice.content = content;
        notice.noticeType = noticeType;
        notice.publisherId = publisherId;
        notice.publishStatus = "DRAFT";
        notice.expireTime = expireTime;
        notice.validate();
        return notice;
    }

    public static Notice reconstitute(Long id, String title, String content,
                                       String noticeType, String publishStatus,
                                       Long publisherId, LocalDateTime publishTime,
                                       LocalDateTime expireTime) {
        Notice notice = new Notice();
        notice.id = id;
        notice.title = title;
        notice.content = content;
        notice.noticeType = noticeType;
        notice.publishStatus = publishStatus;
        notice.publisherId = publisherId;
        notice.publishTime = publishTime;
        notice.expireTime = expireTime;
        return notice;
    }

    public void publish() {
        if ("PUBLISHED".equals(this.publishStatus)) {
            throw new IllegalStateException("公告已发布");
        }
        this.publishStatus = "PUBLISHED";
        this.publishTime = LocalDateTime.now();
        markUpdated();
    }

    public void expire() {
        if (!"PUBLISHED".equals(this.publishStatus)) {
            throw new IllegalStateException("只有已发布状态的公告才能过期");
        }
        this.publishStatus = "EXPIRED";
        markUpdated();
    }

    public boolean isPublished() {
        return "PUBLISHED".equals(this.publishStatus);
    }

    @Override
    protected void validate() {
        if (title == null || title.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "公告标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "公告内容不能为空");
        }
        if (noticeType == null || noticeType.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "公告类型不能为空");
        }
    }
}
