package com.forex.notification.domain.service;

import com.forex.notification.domain.event.NotificationSentEvent;
import com.forex.notification.domain.model.aggregate.Notice;
import com.forex.notification.domain.model.aggregate.Notification;
import com.forex.notification.domain.repository.NoticeRepository;
import com.forex.notification.domain.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDomainService {

    private final NotificationRepository notificationRepository;
    private final NoticeRepository noticeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Notification sendNotification(Notification notification) {
        try {
            notification.send();
            Notification saved = notificationRepository.save(notification);

            eventPublisher.publishEvent(new NotificationSentEvent(
                    saved.getNotifyType(), saved.getBizNo()));

            log.info("通知发送成功: id={}, notifyType={}, bizNo={}",
                    saved.getId(), saved.getNotifyType(), saved.getBizNo());
            return saved;
        } catch (Exception e) {
            log.error("通知发送失败: notifyType={}, bizNo={}, error={}",
                    notification.getNotifyType(), notification.getBizNo(), e.getMessage());
            notification.markFailed(e.getMessage());
            return notificationRepository.save(notification);
        }
    }

    public Notice createNotice(Notice notice) {
        Notice saved = noticeRepository.save(notice);
        log.info("公告创建成功: id={}, title={}, noticeType={}",
                saved.getId(), saved.getTitle(), saved.getNoticeType());
        return saved;
    }

    public Notice publishNotice(Notice notice) {
        notice.publish();
        Notice saved = noticeRepository.save(notice);
        log.info("公告发布成功: id={}, title={}", saved.getId(), saved.getTitle());
        return saved;
    }
}
