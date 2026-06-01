package com.forex.schedule.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_schedule_job")
public class ScheduleJobPO extends BasePO {

    private String jobName;
    private String jobGroup;
    private String jobHandler;
    private String cronExpression;
    private String jobDesc;
    private String status;
    private String lastResult;
    private LocalDateTime lastExecuteTime;
    private LocalDateTime nextExecuteTime;
}
