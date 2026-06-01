package com.forex.notification.adapter.controller;

import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.notification.adapter.dto.CreateNoticeReq;
import com.forex.notification.adapter.dto.NoticeResp;
import com.forex.notification.application.command.CreateNoticeCmd;
import com.forex.notification.application.query.NoticeQuery;
import com.forex.notification.application.service.NotificationAppService;
import com.forex.notification.domain.model.aggregate.Notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "公告管理")
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NotificationAppService notificationAppService;

    @Operation(summary = "创建公告")
    @PostMapping("/create")
    public R<NoticeResp> create(@Valid @RequestBody CreateNoticeReq req) {
        CreateNoticeCmd cmd = new CreateNoticeCmd();
        cmd.setTitle(req.getTitle());
        cmd.setContent(req.getContent());
        cmd.setNoticeType(req.getNoticeType());
        cmd.setPublisherId(req.getPublisherId());
        cmd.setExpireTime(req.getExpireTime());
        Notice notice = notificationAppService.createNotice(cmd);
        return R.ok("公告创建成功", toResp(notice));
    }

    @Operation(summary = "发布公告")
    @PutMapping("/publish/{id}")
    public R<NoticeResp> publish(@PathVariable Long id) {
        Notice notice = notificationAppService.publishNotice(id);
        return R.ok("公告发布成功", toResp(notice));
    }

    @Operation(summary = "查询公告详情")
    @GetMapping("/{id}")
    public R<NoticeResp> getById(@PathVariable Long id) {
        Notice notice = notificationAppService.getNoticeDetail(id);
        return R.ok(toResp(notice));
    }

    @Operation(summary = "分页查询公告")
    @PostMapping("/page")
    public R<PageResp<NoticeResp>> pageQuery(@RequestBody NoticeQuery query) {
        PageResp<Notice> pageResp = notificationAppService.pageQueryNotice(query);
        List<NoticeResp> records = pageResp.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<NoticeResp> resp = PageResp.of(
                pageResp.getTotal(), records, pageResp.getPageNum(), pageResp.getPageSize());
        return R.ok(resp);
    }

    private NoticeResp toResp(Notice notice) {
        NoticeResp resp = new NoticeResp();
        resp.setId(notice.getId());
        resp.setTitle(notice.getTitle());
        resp.setContent(notice.getContent());
        resp.setNoticeType(notice.getNoticeType());
        resp.setPublishStatus(notice.getPublishStatus());
        resp.setPublisherId(notice.getPublisherId());
        resp.setPublishTime(notice.getPublishTime());
        resp.setExpireTime(notice.getExpireTime());
        return resp;
    }
}
