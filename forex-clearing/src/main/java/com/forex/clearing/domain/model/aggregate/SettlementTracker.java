package com.forex.clearing.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class SettlementTracker extends BaseAggregate {

    public static final String STATUS_PENDING_SEND = "PENDING_SEND";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_IN_CLEARING = "IN_CLEARING";
    public static final String STATUS_ACKNOWLEDGED = "ACKNOWLEDGED";
    public static final String STATUS_SETTLED = "SETTLED";
    public static final String STATUS_FUNDS_CREDITED = "FUNDS_CREDITED";
    public static final String STATUS_EXCEPTION = "EXCEPTION";

    private static final Set<String> VALID_STATUSES = Set.of(
            STATUS_PENDING_SEND, STATUS_SENT, STATUS_IN_CLEARING,
            STATUS_ACKNOWLEDGED, STATUS_SETTLED, STATUS_FUNDS_CREDITED, STATUS_EXCEPTION
    );

    private Long id;
    private String trackingId;
    private String paymentNo;
    private String instructionNo;
    private String currentStatus;
    private LocalDateTime statusChangedAt;
    private String channel;
    private String gpiStatus;
    private String exceptionReason;
    private String exceptionDetail;

    private SettlementTracker() {
        super();
    }

    public static SettlementTracker create(String trackingId, String paymentNo,
                                            String instructionNo, String currentStatus,
                                            String channel) {
        SettlementTracker tracker = new SettlementTracker();
        tracker.trackingId = trackingId;
        tracker.paymentNo = paymentNo;
        tracker.instructionNo = instructionNo;
        tracker.currentStatus = currentStatus;
        tracker.statusChangedAt = LocalDateTime.now();
        tracker.channel = channel;
        tracker.validate();
        return tracker;
    }

    public static SettlementTracker reconstitute(Long id, String trackingId, String paymentNo,
                                                  String instructionNo, String currentStatus,
                                                  LocalDateTime statusChangedAt, String channel,
                                                  String gpiStatus, String exceptionReason,
                                                  String exceptionDetail) {
        SettlementTracker tracker = new SettlementTracker();
        tracker.id = id;
        tracker.trackingId = trackingId;
        tracker.paymentNo = paymentNo;
        tracker.instructionNo = instructionNo;
        tracker.currentStatus = currentStatus;
        tracker.statusChangedAt = statusChangedAt;
        tracker.channel = channel;
        tracker.gpiStatus = gpiStatus;
        tracker.exceptionReason = exceptionReason;
        tracker.exceptionDetail = exceptionDetail;
        return tracker;
    }

    public void transitionTo(String newStatus) {
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new BusinessException("无效的结算状态: " + newStatus);
        }
        if (STATUS_EXCEPTION.equals(this.currentStatus)
                && !STATUS_EXCEPTION.equals(newStatus)) {
            this.exceptionReason = null;
            this.exceptionDetail = null;
        }
        this.currentStatus = newStatus;
        this.statusChangedAt = LocalDateTime.now();
        markUpdated();
    }

    public void markException(String reason, String detail) {
        if (STATUS_SETTLED.equals(this.currentStatus)
                || STATUS_FUNDS_CREDITED.equals(this.currentStatus)) {
            throw new BusinessException("已终态的结算无法标记异常");
        }
        this.currentStatus = STATUS_EXCEPTION;
        this.exceptionReason = reason;
        this.exceptionDetail = detail;
        this.statusChangedAt = LocalDateTime.now();
        markUpdated();
    }

    public boolean isOverdue() {
        return !STATUS_SETTLED.equals(this.currentStatus)
                && !STATUS_FUNDS_CREDITED.equals(this.currentStatus);
    }

    @Override
    protected void validate() {
        if (trackingId == null || trackingId.isBlank()) {
            throw new BusinessException("追踪ID不能为空");
        }
        if (paymentNo == null || paymentNo.isBlank()) {
            throw new BusinessException("支付编号不能为空");
        }
        if (currentStatus == null || currentStatus.isBlank()) {
            throw new BusinessException("当前状态不能为空");
        }
        if (!VALID_STATUSES.contains(currentStatus)) {
            throw new BusinessException("无效的结算状态: " + currentStatus);
        }
    }
}
