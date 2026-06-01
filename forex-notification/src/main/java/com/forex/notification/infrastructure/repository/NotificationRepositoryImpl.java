package com.forex.notification.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.notification.application.query.NotifyQuery;
import com.forex.notification.domain.model.aggregate.Notification;
import com.forex.notification.domain.repository.NotificationRepository;
import com.forex.notification.infrastructure.mapper.NotificationMapper;
import com.forex.notification.infrastructure.persistence.NotificationPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationMapper notificationMapper;

    @Override
    public Notification save(Notification notification) {
        NotificationPO po = toPO(notification);
        if (notification.getId() == null) {
            notificationMapper.insert(po);
        } else {
            notificationMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        NotificationPO po = notificationMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<Notification> findByBizNo(String bizNo) {
        NotificationPO po = notificationMapper.selectByBizNo(bizNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<Notification> pageQuery(NotifyQuery query) {
        Page<NotificationPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<NotificationPO> result = notificationMapper.pageQuery(page, query);
        List<Notification> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private Notification toDomain(NotificationPO po) {
        return Notification.reconstitute(
                po.getId(),
                po.getTitle(),
                po.getContent(),
                po.getNotifyType(),
                po.getTargetUsers(),
                po.getTargetUserNames(),
                po.getBizType(),
                po.getBizNo(),
                po.getStatus(),
                po.getSendTime(),
                po.getFailedReason()
        );
    }

    private NotificationPO toPO(Notification notification) {
        NotificationPO po = new NotificationPO();
        po.setId(notification.getId());
        po.setTitle(notification.getTitle());
        po.setContent(notification.getContent());
        po.setNotifyType(notification.getNotifyType());
        po.setTargetUsers(notification.getTargetUsers());
        po.setTargetUserNames(notification.getTargetUserNames());
        po.setBizType(notification.getBizType());
        po.setBizNo(notification.getBizNo());
        po.setStatus(notification.getStatus());
        po.setSendTime(notification.getSendTime());
        po.setFailedReason(notification.getFailedReason());
        return po;
    }
}
