package com.forex.notification.domain.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.notification.application.query.NoticeQuery;
import com.forex.notification.domain.model.aggregate.Notice;

import java.util.Optional;

public interface NoticeRepository {

    Notice save(Notice notice);

    Optional<Notice> findById(Long id);

    PageResp<Notice> pageQuery(NoticeQuery query);
}
