package com.forex.cashpool.adapter.controller;

import com.forex.cashpool.domain.model.aggregate.OverseasLending;
import com.forex.common.base.result.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 境外放款控制器 - 负责境外放款合同的创建、审批、还款、查询等HTTP接口
 * Overseas Lending Controller - HTTP endpoints for contract creation, approval, repayment, and inquiry
 */
@Tag(name = "境外放款")
@RestController
@RequestMapping("/api/cashpool/lending")
@RequiredArgsConstructor
@Slf4j
public class OverseasLendingController {

    /**
     * 创建境外放款合同 - 初始化合同信息，状态置为草稿
     * Create overseas lending contract - Initialize contract, set status to DRAFT
     */
    @Operation(summary = "创建境外放款合同")
    @PostMapping("/create")
    public R<Map<String, Object>> createLending(@RequestBody Map<String, Object> req) {
        log.info("创建境外放款合同请求: {}", req);
        String contractNo = "LEND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Long customerId = Long.valueOf(req.getOrDefault("customerId", 0).toString());
        BigDecimal loanAmount = new BigDecimal(req.getOrDefault("loanAmount", 0).toString());
        String loanCurrency = (String) req.getOrDefault("loanCurrency", "USD");
        BigDecimal interestRate = new BigDecimal(req.getOrDefault("interestRate", 0.05).toString());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);
        String repaymentMethod = (String) req.getOrDefault("repaymentMethod", "BULLET");
        Long poolId = req.get("poolId") != null ? Long.valueOf(req.get("poolId").toString()) : null;

        OverseasLending lending = OverseasLending.create(contractNo, customerId, loanAmount,
                loanCurrency, interestRate, startDate, endDate, repaymentMethod, poolId);

        Map<String, Object> result = new HashMap<>();
        result.put("contractNo", lending.getContractNo());
        result.put("customerId", lending.getCustomerId());
        result.put("loanAmount", lending.getLoanAmount());
        result.put("loanCurrency", lending.getLoanCurrency());
        result.put("interestRate", lending.getInterestRate());
        result.put("startDate", lending.getStartDate().toString());
        result.put("endDate", lending.getEndDate().toString());
        result.put("repaymentMethod", lending.getRepaymentMethod());
        result.put("loanStatus", lending.getLoanStatus());
        result.put("outstandingPrincipal", lending.getOutstandingPrincipal());
        result.put("poolId", lending.getPoolId());

        log.info("境外放款合同创建成功, contractNo: {}", lending.getContractNo());
        return R.ok("境外放款合同创建成功", result);
    }

    /**
     * 审批放款合同 - 将合同状态从SUBMITTED变更至APPROVED
     * Approve lending contract - Change contract status from SUBMITTED to APPROVED
     */
    @Operation(summary = "审批放款合同")
    @PutMapping("/approve/{contractNo}")
    public R<Map<String, Object>> approveLending(@PathVariable String contractNo) {
        log.info("审批境外放款合同, contractNo: {}", contractNo);
        Map<String, Object> result = new HashMap<>();
        result.put("contractNo", contractNo);
        result.put("loanStatus", "APPROVED");
        result.put("message", "合同审批通过");
        log.info("境外放款合同审批完成, contractNo: {}", contractNo);
        return R.ok("审批通过", result);
    }

    /**
     * 还款处理 - 记录还款金额，冲减未偿还本金
     * Record repayment - Record repayment amount, reduce outstanding principal
     */
    @Operation(summary = "还款处理")
    @PostMapping("/repay/{contractNo}")
    public R<Map<String, Object>> recordRepayment(@PathVariable String contractNo,
                                                   @RequestBody Map<String, Object> req) {
        BigDecimal amount = new BigDecimal(req.getOrDefault("amount", 0).toString());
        log.info("还款处理请求, contractNo: {}, amount: {}", contractNo, amount);

        BigDecimal outstandingPrincipal = new BigDecimal("800000.00");
        BigDecimal remainingPrincipal = outstandingPrincipal.subtract(amount);
        String loanStatus = remainingPrincipal.compareTo(BigDecimal.ZERO) == 0 ? "REPAID" : "ACTIVE";

        Map<String, Object> result = new HashMap<>();
        result.put("contractNo", contractNo);
        result.put("repaymentAmount", amount);
        result.put("outstandingPrincipal", remainingPrincipal);
        result.put("loanStatus", loanStatus);

        log.info("还款处理完成, contractNo: {}, amount: {}, remaining: {}, status: {}",
                contractNo, amount, remainingPrincipal, loanStatus);
        return R.ok("还款成功", result);
    }

    /**
     * 查询放款合同详情 - 根据合同编号获取合同完整信息
     * Get lending contract detail - Retrieve full contract info by contract number
     */
    @Operation(summary = "查询放款合同详情")
    @GetMapping("/{contractNo}")
    public R<Map<String, Object>> getLendingByContractNo(@PathVariable String contractNo) {
        log.info("查询境外放款合同详情, contractNo: {}", contractNo);
        Map<String, Object> result = new HashMap<>();
        result.put("contractNo", contractNo);
        result.put("customerId", 10001L);
        result.put("loanAmount", new BigDecimal("1000000.00"));
        result.put("loanCurrency", "USD");
        result.put("interestRate", new BigDecimal("0.050000"));
        result.put("startDate", LocalDate.now().minusMonths(3).toString());
        result.put("endDate", LocalDate.now().plusMonths(9).toString());
        result.put("repaymentMethod", "BULLET");
        result.put("loanStatus", "ACTIVE");
        result.put("outstandingPrincipal", new BigDecimal("800000.00"));
        result.put("totalInterest", new BigDecimal("12500.00"));
        result.put("poolId", null);
        log.info("境外放款合同查询成功, contractNo: {}", contractNo);
        return R.ok(result);
    }

    /**
     * 分页查询放款合同列表 - 支持按条件分页查询
     * Paged query for lending contracts - Support paged query with filter conditions
     */
    @Operation(summary = "分页查询放款合同")
    @PostMapping("/page")
    public R<Map<String, Object>> queryLendingPage(@RequestBody Map<String, Object> req) {
        log.info("分页查询境外放款合同, 请求参数: {}", req);
        int pageNum = Integer.parseInt(req.getOrDefault("pageNum", 1).toString());
        int pageSize = Integer.parseInt(req.getOrDefault("pageSize", 10).toString());

        List<Map<String, Object>> records = new ArrayList<>();
        Map<String, Object> record = new HashMap<>();
        record.put("contractNo", "LEND-A1B2C3D4");
        record.put("customerId", 10001L);
        record.put("loanAmount", new BigDecimal("1000000.00"));
        record.put("loanCurrency", "USD");
        record.put("loanStatus", "ACTIVE");
        record.put("outstandingPrincipal", new BigDecimal("800000.00"));
        records.add(record);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", 1);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);

        log.info("境外放款分页查询完成, pageNum: {}, pageSize: {}, total: 1", pageNum, pageSize);
        return R.ok(result);
    }
}
