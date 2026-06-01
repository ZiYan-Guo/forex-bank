package com.forex.workflow.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApprovalRecord extends BaseEntity {

    private Long id;
    private Long taskId;
    private String stepName;
    private String approver;
    private String approverName;
    private String approveResult;
    private String comment;
    private LocalDateTime approveTime;

    public ApprovalRecord(Long id, Long taskId, String stepName, String approver,
                          String approverName, String approveResult, String comment,
                          LocalDateTime approveTime) {
        this.id = id;
        this.taskId = taskId;
        this.stepName = stepName;
        this.approver = approver;
        this.approverName = approverName;
        this.approveResult = approveResult;
        this.comment = comment;
        this.approveTime = approveTime;
    }
}
