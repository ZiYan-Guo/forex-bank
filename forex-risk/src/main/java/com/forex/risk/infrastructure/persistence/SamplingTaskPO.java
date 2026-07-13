package com.forex.risk.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Sampling task persistent object.
 * 抽查任务持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sampling_task")
public class SamplingTaskPO extends BasePO {

    private String taskId;
    private String bizNo;
    private String bizType;
    private Long customerId;
    private BigDecimal amount;
    private String currency;
    private String countryCode;
    private Integer accountAgeDays;
    private BigDecimal samplingRate;
    private String reason;
    private String matchedRulesJson;
    private String status;
    private LocalDate businessDate;
    private LocalDateTime completedAt;
    private String reviewResult;
    private String reviewComment;
}
