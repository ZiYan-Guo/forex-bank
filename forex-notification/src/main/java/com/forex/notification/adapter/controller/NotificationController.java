package com.forex.notification.adapter.controller;

import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.notification.adapter.dto.NotificationResp;
import com.forex.notification.adapter.dto.SendNotificationReq;
import com.forex.notification.application.command.SendNotificationCmd;
import com.forex.notification.application.query.NotifyQuery;
import com.forex.notification.application.service.NotificationAppService;
import com.forex.notification.domain.model.aggregate.Notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "通知管理")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationAppService notificationAppService;

    @Operation(summary = "发送通知")
    @PostMapping("/send")
    public R<NotificationResp> send(@Valid @RequestBody SendNotificationReq req) {
        SendNotificationCmd cmd = new SendNotificationCmd();
        cmd.setTitle(req.getTitle());
        cmd.setContent(req.getContent());
        cmd.setNotifyType(req.getNotifyType());
        cmd.setTargetUsers(req.getTargetUsers());
        cmd.setTargetUserNames(req.getTargetUserNames());
        cmd.setBizType(req.getBizType());
        cmd.setBizNo(req.getBizNo());
        Notification notification = notificationAppService.sendNotification(cmd);
        return R.ok("通知发送成功", toResp(notification));
    }

    @Operation(summary = "查询通知详情")
    @GetMapping("/{id}")
    public R<NotificationResp> getById(@PathVariable Long id) {
        Notification notification = notificationAppService.getNotificationDetail(id);
        return R.ok(toResp(notification));
    }

    @Operation(summary = "分页查询通知")
    @PostMapping("/page")
    public R<PageResp<NotificationResp>> pageQuery(@RequestBody NotifyQuery query) {
        PageResp<Notification> pageResp = notificationAppService.pageQueryNotification(query);
        List<NotificationResp> records = pageResp.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<NotificationResp> resp = PageResp.of(
                pageResp.getTotal(), records, pageResp.getPageNum(), pageResp.getPageSize());
        return R.ok(resp);
    }

    private NotificationResp toResp(Notification notification) {
        NotificationResp resp = new NotificationResp();
        resp.setId(notification.getId());
        resp.setTitle(notification.getTitle());
        resp.setContent(notification.getContent());
        resp.setNotifyType(notification.getNotifyType());
        resp.setTargetUsers(notification.getTargetUsers());
        resp.setTargetUserNames(notification.getTargetUserNames());
        resp.setBizType(notification.getBizType());
        resp.setBizNo(notification.getBizNo());
        resp.setStatus(notification.getStatus());
        resp.setSendTime(notification.getSendTime());
        resp.setFailedReason(notification.getFailedReason());
        return resp;
    }
}
