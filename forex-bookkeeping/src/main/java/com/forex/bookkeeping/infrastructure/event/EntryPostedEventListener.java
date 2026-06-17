package com.forex.bookkeeping.infrastructure.event;

import com.forex.bookkeeping.domain.event.EntryPostedEvent;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for posted journal entry events.
 * When a journal entry is posted, this listener logs the event and
 * can be extended to trigger cross-module reactions such as
 * updating account balances or notifying downstream systems.
 * 会计分录过账事件监听器。
 * 当分录过账时记录日志，可扩展为跨模块联动（如更新账户余额、通知下游系统）。
 */
@Slf4j
@Component
public class EntryPostedEventListener {

    /**
     * Handle entry posted events — log key details.
     * 处理分录过账事件 —— 记录关键信息。
     */
    @EventListener
    public void onEntryPosted(EntryPostedEvent event) {
        log.info("Entry posted: entryId={}, voucherNo={}, amount={}",
                event.getEntryId(), event.getVoucherNo(), event.getAmount());
    }
}
