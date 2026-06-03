package com.forex.clearing.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ClsSession extends BaseAggregate {

    public static final String STATUS_SCHEDULED = "SCHEDULED";
    public static final String STATUS_PAY_IN_OPEN = "PAY_IN_OPEN";
    public static final String STATUS_PAY_IN_CLOSED = "PAY_IN_CLOSED";
    public static final String STATUS_CALCULATING = "CALCULATING";
    public static final String STATUS_PAY_OUT_READY = "PAY_OUT_READY";
    public static final String STATUS_COMPLETED = "COMPLETED";

    private Long id;
    private String sessionId;
    private LocalDate settlementDate;
    private LocalDateTime payInWindowStart;
    private LocalDateTime payInWindowEnd;
    private String sessionStatus;
    private BigDecimal totalPayInSum;
    private BigDecimal totalPayOutSum;
    private BigDecimal netPosition;
    private String positionJson;

    private ClsSession() {
        super();
    }

    public static ClsSession create(String sessionId, LocalDate settlementDate) {
        ClsSession session = new ClsSession();
        session.sessionId = sessionId;
        session.settlementDate = settlementDate;
        session.sessionStatus = STATUS_SCHEDULED;
        session.totalPayInSum = BigDecimal.ZERO;
        session.totalPayOutSum = BigDecimal.ZERO;
        session.netPosition = BigDecimal.ZERO;
        session.validate();
        return session;
    }

    public static ClsSession reconstitute(Long id, String sessionId, LocalDate settlementDate,
                                           LocalDateTime payInWindowStart, LocalDateTime payInWindowEnd,
                                           String sessionStatus, BigDecimal totalPayInSum,
                                           BigDecimal totalPayOutSum, BigDecimal netPosition,
                                           String positionJson) {
        ClsSession session = new ClsSession();
        session.id = id;
        session.sessionId = sessionId;
        session.settlementDate = settlementDate;
        session.payInWindowStart = payInWindowStart;
        session.payInWindowEnd = payInWindowEnd;
        session.sessionStatus = sessionStatus;
        session.totalPayInSum = totalPayInSum;
        session.totalPayOutSum = totalPayOutSum;
        session.netPosition = netPosition;
        session.positionJson = positionJson;
        return session;
    }

    public void openPayIn() {
        if (!STATUS_SCHEDULED.equals(this.sessionStatus)) {
            throw new BusinessException("只有已排期状态才能开启Pay-In窗口");
        }
        this.sessionStatus = STATUS_PAY_IN_OPEN;
        this.payInWindowStart = LocalDateTime.now();
        markUpdated();
    }

    public void closePayIn() {
        if (!STATUS_PAY_IN_OPEN.equals(this.sessionStatus)) {
            throw new BusinessException("只有Pay-In开启状态才能关闭窗口");
        }
        this.sessionStatus = STATUS_PAY_IN_CLOSED;
        this.payInWindowEnd = LocalDateTime.now();
        markUpdated();
    }

    public void calculateNetPositions() {
        if (!STATUS_PAY_IN_CLOSED.equals(this.sessionStatus)) {
            throw new BusinessException("只有Pay-In关闭状态才能计算净额");
        }
        this.sessionStatus = STATUS_CALCULATING;
        markUpdated();
    }

    public void completeSettlement() {
        if (!STATUS_CALCULATING.equals(this.sessionStatus)
                && !STATUS_PAY_OUT_READY.equals(this.sessionStatus)) {
            throw new BusinessException("当前状态不能完成结算");
        }
        this.sessionStatus = STATUS_COMPLETED;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (sessionId == null || sessionId.isBlank()) {
            throw new BusinessException("场次ID不能为空");
        }
        if (settlementDate == null) {
            throw new BusinessException("结算日期不能为空");
        }
    }
}
