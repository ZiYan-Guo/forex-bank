package com.forex.bookkeeping.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Month-end closing aggregate root.
 * Manages the month-end closing lifecycle: OPEN → IN_PROGRESS → COMPLETED → LOCKED.
 * 月末结账聚合根。管理月末结账生命周期。
 */
@Getter
public class MonthEndClosing extends BaseAggregate {

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_LOCKED = "LOCKED";

    private Long id;
    private String closingId;
    private String fiscalPeriod;
    private LocalDate closingDate;
    private String closingStatus;
    private String checklistJson;
    private String auditTrail;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private Long operatorId;

    private MonthEndClosing() {
        super();
    }

    public static MonthEndClosing create(String fiscalPeriod, LocalDate closingDate, Long operatorId) {
        MonthEndClosing closing = new MonthEndClosing();
        closing.closingId = "CLS" + fiscalPeriod + System.currentTimeMillis() % 100000;
        closing.fiscalPeriod = fiscalPeriod;
        closing.closingDate = closingDate;
        closing.closingStatus = STATUS_OPEN;
        closing.checklistJson = "{}";
        closing.auditTrail = "";
        closing.totalDebits = BigDecimal.ZERO;
        closing.totalCredits = BigDecimal.ZERO;
        closing.operatorId = operatorId;
        closing.validate();
        return closing;
    }

    public static MonthEndClosing reconstitute(Long id, String closingId, String fiscalPeriod,
                                                LocalDate closingDate, String closingStatus,
                                                String checklistJson, String auditTrail,
                                                BigDecimal totalDebits, BigDecimal totalCredits,
                                                Long operatorId) {
        MonthEndClosing closing = new MonthEndClosing();
        closing.id = id;
        closing.closingId = closingId;
        closing.fiscalPeriod = fiscalPeriod;
        closing.closingDate = closingDate;
        closing.closingStatus = closingStatus;
        closing.checklistJson = checklistJson;
        closing.auditTrail = auditTrail;
        closing.totalDebits = totalDebits;
        closing.totalCredits = totalCredits;
        closing.operatorId = operatorId;
        return closing;
    }

    public void start() {
        if (!STATUS_OPEN.equals(this.closingStatus)) {
            throw new BusinessException("只有OPEN状态的结账记录才能启动");
        }
        this.closingStatus = STATUS_IN_PROGRESS;
        markUpdated();
    }

    public void complete(String checklist) {
        if (!STATUS_IN_PROGRESS.equals(this.closingStatus)) {
            throw new BusinessException("只有IN_PROGRESS状态的结账记录才能完成");
        }
        this.closingStatus = STATUS_COMPLETED;
        this.checklistJson = checklist;
        markUpdated();
    }

    public void lock() {
        if (!STATUS_COMPLETED.equals(this.closingStatus)) {
            throw new BusinessException("只有COMPLETED状态的结账记录才能锁定");
        }
        this.closingStatus = STATUS_LOCKED;
        markUpdated();
    }

    public void addAuditEntry(String entry) {
        String timestamp = LocalDateTime.now().toString();
        if (this.auditTrail == null || this.auditTrail.isEmpty()) {
            this.auditTrail = "[" + timestamp + "] " + entry;
        } else {
            this.auditTrail += "\n[" + timestamp + "] " + entry;
        }
        markUpdated();
    }

    public void setTotalDebits(BigDecimal totalDebits) {
        this.totalDebits = totalDebits;
        markUpdated();
    }

    public void setTotalCredits(BigDecimal totalCredits) {
        this.totalCredits = totalCredits;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (fiscalPeriod == null || fiscalPeriod.isBlank()) {
            throw new BusinessException("会计期间不能为空");
        }
        if (closingDate == null) {
            throw new BusinessException("结账日期不能为空");
        }
    }
}
