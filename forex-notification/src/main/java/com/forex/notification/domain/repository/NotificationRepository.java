package com.forex.notification.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.notification.application.query.NotifyQuery;
import com.forex.notification.domain.model.aggregate.Notification;

import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    Optional<Notification> findByBizNo(String bizNo);

    PageResp<Notification> pageQuery(NotifyQuery query);
}
