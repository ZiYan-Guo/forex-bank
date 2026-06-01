package com.forex.notification.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.notification.application.query.NoticeQuery;
import com.forex.notification.domain.model.aggregate.Notice;
import com.forex.notification.domain.repository.NoticeRepository;
import com.forex.notification.infrastructure.mapper.NoticeMapper;
import com.forex.notification.infrastructure.persistence.NoticePO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeMapper noticeMapper;

    @Override
    public Notice save(Notice notice) {
        NoticePO po = toPO(notice);
        if (notice.getId() == null) {
            noticeMapper.insert(po);
        } else {
            noticeMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<Notice> findById(Long id) {
        NoticePO po = noticeMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<Notice> pageQuery(NoticeQuery query) {
        Page<NoticePO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<NoticePO> result = noticeMapper.pageQuery(page, query);
        List<Notice> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private Notice toDomain(NoticePO po) {
        return Notice.reconstitute(
                po.getId(),
                po.getTitle(),
                po.getContent(),
                po.getNoticeType(),
                po.getPublishStatus(),
                po.getPublisherId(),
                po.getPublishTime(),
                po.getExpireTime()
        );
    }

    private NoticePO toPO(Notice notice) {
        NoticePO po = new NoticePO();
        po.setId(notice.getId());
        po.setTitle(notice.getTitle());
        po.setContent(notice.getContent());
        po.setNoticeType(notice.getNoticeType());
        po.setPublishStatus(notice.getPublishStatus());
        po.setPublisherId(notice.getPublisherId());
        po.setPublishTime(notice.getPublishTime());
        po.setExpireTime(notice.getExpireTime());
        return po;
    }
}
