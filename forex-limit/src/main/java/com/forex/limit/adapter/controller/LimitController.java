package com.forex.limit.adapter.controller;

import com.forex.common.base.result.R;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.limit.application.command.CreateLimitCmd;
import com.forex.limit.application.service.LimitAppService;
import com.forex.limit.domain.model.aggregate.LimitConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "限额管理")
@RestController
@RequestMapping("/api/limit")
@RequiredArgsConstructor
public class LimitController {

    private final LimitAppService limitAppService;

    @Operation(summary = "创建限额配置")
    @PostMapping("/create")
    @RequirePermission("limit:config:create")
    public R<String> createLimit(@Valid @RequestBody CreateLimitCmd cmd) {
        return R.ok(limitAppService.createLimit(cmd));
    }

    @Operation(summary = "审批限额")
    @PostMapping("/approve/{limitNo}")
    @RequirePermission("limit:config:approve")
    public R<Void> approveLimit(@PathVariable String limitNo) {
        limitAppService.approveLimit(limitNo);
        return R.ok();
    }

    @Operation(summary = "检查限额")
    @GetMapping("/check")
    public R<Boolean> checkLimit(@RequestParam Long customerId,
                                  @RequestParam String limitType,
                                  @RequestParam String dimension,
                                  @RequestParam String dimensionValue,
                                  @RequestParam java.math.BigDecimal amount) {
        return R.ok(limitAppService.checkLimit(customerId, limitType, dimension, dimensionValue, amount));
    }

    @Operation(summary = "查询客户限额")
    @GetMapping("/customer/{customerId}")
    public R<List<LimitConfig>> getCustomerLimits(@PathVariable Long customerId) {
        return R.ok(limitAppService.getCustomerLimits(customerId));
    }
}
