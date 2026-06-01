package com.forex.schedule.domain.model.aggregate;

import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class JobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long jobId;
    private String jobName;
    private String jobHandler;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String executeStatus;
    private String executeResult;
    private String errorMsg;

    private JobLog() {
    }

    public static JobLog create(Long jobId, String jobName, String jobHandler) {
        JobLog log = new JobLog();
        log.jobId = jobId;
        log.jobName = jobName;
        log.jobHandler = jobHandler;
        log.startTime = LocalDateTime.now();
        log.executeStatus = "RUNNING";
        return log;
    }

    public static JobLog reconstitute(Long id, Long jobId, String jobName, String jobHandler,
                                       LocalDateTime startTime, LocalDateTime endTime,
                                       String executeStatus, String executeResult, String errorMsg) {
        JobLog log = new JobLog();
        log.id = id;
        log.jobId = jobId;
        log.jobName = jobName;
        log.jobHandler = jobHandler;
        log.startTime = startTime;
        log.endTime = endTime;
        log.executeStatus = executeStatus;
        log.executeResult = executeResult;
        log.errorMsg = errorMsg;
        return log;
    }

    public void markSuccess(String result) {
        this.executeStatus = "SUCCESS";
        this.executeResult = result;
        this.endTime = LocalDateTime.now();
    }

    public void markFailed(String error) {
        this.executeStatus = "FAILED";
        this.errorMsg = error;
        this.endTime = LocalDateTime.now();
    }
}
