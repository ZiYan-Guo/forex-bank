package com.forex.risk.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Sampling inspection task entity.
 * 便利化抽查任务实体。
 */
@Getter
@NoArgsConstructor
public class SamplingTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Primary key ID 主键ID */
    private Long id;

    /** Stable task identifier 稳定任务编号 */
    private String taskId;

    /** Business transaction number 业务编号 */
    private String bizNo;

    /** Business module 业务模块 */
    private String bizType;

    /** Customer identifier 客户ID */
    private Long customerId;

    /** Transaction amount 交易金额 */
    private BigDecimal amount;

    /** Transaction currency 交易币种 */
    private String currency;

    /** Counterparty country code 交易对手国家代码 */
    private String countryCode;

    /** Customer account age in days 客户开户天数 */
    private Integer accountAgeDays;

    /** Applied sampling rate 抽查比例 */
    private BigDecimal samplingRate;

    /** Human-readable sampling reason 抽查原因 */
    private String reason;

    /** Matched sampling rule codes 命中的抽查规则编码 */
    private List<String> matchedRules;

    /** Task status: PENDING/COMPLETED 任务状态 */
    private String status;

    /** Business date 业务日期 */
    private LocalDate businessDate;

    /** Task created time 任务创建时间 */
    private LocalDateTime createdAt;

    /** Completion time 完成时间 */
    private LocalDateTime completedAt;

    /** Review result 检查结果 */
    private String reviewResult;

    /** Review comment 检查意见 */
    private String reviewComment;

    public SamplingTask(Long id, String taskId, String bizNo, String bizType, Long customerId,
                        BigDecimal amount, String currency, String countryCode, Integer accountAgeDays,
                        BigDecimal samplingRate, String reason, List<String> matchedRules, String status,
                        LocalDate businessDate, LocalDateTime createdAt, LocalDateTime completedAt,
                        String reviewResult, String reviewComment) {
        this.id = id;
        this.taskId = taskId;
        this.bizNo = bizNo;
        this.bizType = bizType;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.countryCode = countryCode;
        this.accountAgeDays = accountAgeDays;
        this.samplingRate = samplingRate;
        this.reason = reason;
        this.matchedRules = matchedRules;
        this.status = status;
        this.businessDate = businessDate;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.reviewResult = reviewResult;
        this.reviewComment = reviewComment;
    }

    /**
     * Mark task as completed and keep the reviewer conclusion.
     * 将任务标记为已完成，并记录检查结论。
     */
    public SamplingTask complete(String result, String comment) {
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
        this.reviewResult = result == null || result.isBlank() ? "PASS" : result;
        this.reviewComment = comment == null ? "" : comment;
        return this;
    }
}
