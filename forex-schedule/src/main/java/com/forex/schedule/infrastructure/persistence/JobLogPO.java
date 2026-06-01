package com.forex.schedule.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_job_log")
public class JobLogPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long jobId;
    private String jobName;
    private String jobHandler;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String executeStatus;
    private String executeResult;
    private String errorMsg;
    private LocalDateTime createTime;
}
