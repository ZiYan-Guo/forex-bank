package com.forex.clearing.adapter.controller;

import com.forex.clearing.application.service.RouteOptimizationService;
import com.forex.clearing.domain.model.valueobject.SettlementRoute;
import com.forex.common.base.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "路由优选")
@RestController
@RequestMapping("/api/payment/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteOptimizationService routeOptimizationService;

    @Operation(summary = "优化结算路由")
    @RequirePermission("clearing:optimize")
    @PostMapping("/optimize")
    public R<SettlementRoute> optimize(@RequestBody Map<String, Object> body) {
        String payCurrency = (String) body.get("payCurrency");
        String receiveCurrency = (String) body.get("receiveCurrency");
        BigDecimal amount = body.get("amount") != null
                ? new BigDecimal(body.get("amount").toString()) : BigDecimal.ZERO;
        String country = (String) body.get("country");
        SettlementRoute route = routeOptimizationService.optimizeRoute(
                payCurrency, receiveCurrency, amount, country);
        return R.ok(route);
    }
}
