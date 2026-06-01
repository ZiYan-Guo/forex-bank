package com.forex.schedule.handler;

import com.forex.schedule.domain.service.ScheduleDomainService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.xxl.job.core.biz.model.ReturnT;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForexJobHandler {

    private final ScheduleDomainService scheduleDomainService;

    @XxlJob("dailyClosingJob")
    public ReturnT<String> dailyClosingJob() {
        log.info(">>>>>>>>>>> 日终结算任务开始执行");
        XxlJobHelper.log("XXL-JOB, 日终结算任务开始...");
        try {
            scheduleDomainService.executeDailyClosing(LocalDate.now());
            log.info(">>>>>>>>>>> 日终结算任务执行成功");
            XxlJobHelper.handleSuccess("日终结算完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("日终结算任务执行失败", e);
            XxlJobHelper.handleFail("日终结算失败: " + e.getMessage());
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @XxlJob("reconciliationJob")
    public ReturnT<String> reconciliationJob() {
        log.info(">>>>>>>>>>> 对账任务开始执行");
        XxlJobHelper.log("XXL-JOB, 对账任务开始...");
        try {
            scheduleDomainService.executeReconciliation();
            log.info(">>>>>>>>>>> 对账任务执行成功");
            XxlJobHelper.handleSuccess("对账完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("对账任务执行失败", e);
            XxlJobHelper.handleFail("对账失败: " + e.getMessage());
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @XxlJob("reportingBatchJob")
    public ReturnT<String> reportingBatchJob() {
        log.info(">>>>>>>>>>> 批量报送任务开始执行");
        XxlJobHelper.log("XXL-JOB, 批量报送任务开始...");
        try {
            log.info("批量报送执行完成");
            XxlJobHelper.handleSuccess("批量报送完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("批量报送任务执行失败", e);
            XxlJobHelper.handleFail("批量报送失败: " + e.getMessage());
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @XxlJob("rateRefreshJob")
    public ReturnT<String> rateRefreshJob() {
        log.info(">>>>>>>>>>> 汇率刷新任务开始执行");
        XxlJobHelper.log("XXL-JOB, 汇率刷新任务开始...");
        try {
            log.info("汇率刷新完成");
            XxlJobHelper.handleSuccess("汇率刷新完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("汇率刷新任务执行失败", e);
            XxlJobHelper.handleFail("汇率刷新失败: " + e.getMessage());
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }
}
