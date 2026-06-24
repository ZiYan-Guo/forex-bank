package com.forex.notification.domain.service;

import com.forex.notification.domain.event.NotificationSentEvent;
import com.forex.notification.domain.model.aggregate.Notice;
import com.forex.notification.domain.model.aggregate.Notification;
import com.forex.notification.domain.repository.NoticeRepository;
import com.forex.notification.domain.repository.NotificationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationDomainServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private NoticeRepository noticeRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private NotificationDomainService notificationDomainService;

    @Test
    @DisplayName("Send notification saves and publishes event")
    void testSendNotification_Success() {
        Notification notification = mock(Notification.class);
        when(notificationRepository.save(any())).thenReturn(notification);

        Notification result = notificationDomainService.sendNotification(notification);

        verify(notification).send();
        verify(notificationRepository).save(notification);
        verify(eventPublisher).publishEvent(any(NotificationSentEvent.class));
    }

    @Test
    @DisplayName("Send notification marks failed on exception")
    void testSendNotification_Failure() {
        Notification notification = mock(Notification.class);
        doThrow(new RuntimeException("SMTP error")).when(notification).send();
        when(notificationRepository.save(any())).thenReturn(notification);

        notificationDomainService.sendNotification(notification);

        verify(notification).markFailed(contains("SMTP error"));
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Create notice saves and returns")
    void testCreateNotice() {
        Notice notice = mock(Notice.class);
        when(noticeRepository.save(any())).thenReturn(notice);

        Notice result = notificationDomainService.createNotice(notice);
        assertNotNull(result);
        verify(noticeRepository).save(notice);
    }

    @Test
    @DisplayName("Publish notice calls publish and saves")
    void testPublishNotice() {
        Notice notice = mock(Notice.class);
        when(noticeRepository.save(any())).thenReturn(notice);

        Notice result = notificationDomainService.publishNotice(notice);
        verify(notice).publish();
        verify(noticeRepository).save(notice);
    }
}
