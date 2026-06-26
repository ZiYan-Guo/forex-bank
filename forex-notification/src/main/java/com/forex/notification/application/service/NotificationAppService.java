package com.forex.notification.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.notification.application.command.CreateNoticeCmd;
import com.forex.notification.application.command.SendNotificationCmd;
import com.forex.notification.application.query.NoticeQuery;
import com.forex.notification.application.query.NotifyQuery;
import com.forex.notification.domain.model.aggregate.Notice;
import com.forex.notification.domain.model.aggregate.Notification;
import com.forex.notification.domain.repository.NoticeRepository;
import com.forex.notification.domain.repository.NotificationRepository;
import com.forex.notification.domain.service.NotificationDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Service
@RequiredArgsConstructor
public class NotificationAppService {

    private final NotificationDomainService notificationDomainService;
    private final NotificationRepository notificationRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public Notification sendNotification(SendNotificationCmd cmd) {
        Notification notification = Notification.create(
                cmd.getTitle(),
                cmd.getContent(),
                cmd.getNotifyType(),
                cmd.getTargetUsers(),
                cmd.getTargetUserNames(),
                cmd.getBizType(),
                cmd.getBizNo()
        );
        return notificationDomainService.sendNotification(notification);
    }

    @Transactional
    public Notice createNotice(CreateNoticeCmd cmd) {
        Notice notice = Notice.create(
                cmd.getTitle(),
                cmd.getContent(),
                cmd.getNoticeType(),
                cmd.getPublisherId(),
                cmd.getExpireTime()
        );
        return notificationDomainService.createNotice(notice);
    }

    @Transactional
    public Notice publishNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "公告不存在"));
        return notificationDomainService.publishNotice(notice);
    }

    public Notification getNotificationDetail(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "通知不存在"));
    }

    public Notice getNoticeDetail(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "公告不存在"));
    }

    public PageResp<Notification> pageQueryNotification(NotifyQuery query) {
        return notificationRepository.pageQuery(query);
    }

    public PageResp<Notice> pageQueryNotice(NoticeQuery query) {
        return noticeRepository.pageQuery(query);
    }
}
