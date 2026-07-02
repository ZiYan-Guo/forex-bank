package com.forex.preciousmetal.adapter.controller;

import com.forex.common.base.result.R;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.preciousmetal.application.command.CreateOrderCmd;
import com.forex.preciousmetal.application.command.SettleOrderCmd;
import com.forex.preciousmetal.application.query.OrderQuery;
import com.forex.preciousmetal.application.service.MetalAppService;
import com.forex.preciousmetal.domain.model.aggregate.PreciousMetalOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "贵金属交易")
@RestController
@RequestMapping("/api/precious-metal")
@RequiredArgsConstructor
public class PreciousMetalController {

    private final MetalAppService metalAppService;

    @Operation(summary = "创建贵金属订单")
    @PostMapping("/order/create")
    @RequirePermission("precious-metal:order:create")
    public R<String> createOrder(@Valid @RequestBody CreateOrderCmd cmd) {
        String orderNo = metalAppService.createOrder(cmd);
        return R.ok(orderNo);
    }

    @Operation(summary = "确认订单")
    @PostMapping("/order/confirm/{orderNo}")
    @RequirePermission("precious-metal:order:confirm")
    public R<Void> confirmOrder(@PathVariable String orderNo) {
        metalAppService.confirmOrder(orderNo);
        return R.ok();
    }

    @Operation(summary = "执行订单")
    @PostMapping("/order/execute/{orderNo}")
    @RequirePermission("precious-metal:order:execute")
    public R<Void> executeOrder(@PathVariable String orderNo) {
        metalAppService.executeOrder(orderNo);
        return R.ok();
    }

    @Operation(summary = "取消订单")
    @PostMapping("/order/cancel/{orderNo}")
    @RequirePermission("precious-metal:order:cancel")
    public R<Void> cancelOrder(@PathVariable String orderNo) {
        metalAppService.cancelOrder(orderNo);
        return R.ok();
    }

    @Operation(summary = "订单结算")
    @PostMapping("/order/settle")
    @RequirePermission("precious-metal:order:settle")
    public R<Void> settleOrder(@Valid @RequestBody SettleOrderCmd cmd) {
        metalAppService.settleOrder(cmd);
        return R.ok();
    }

    @Operation(summary = "查询订单详情")
    @GetMapping("/order/{orderNo}")
    public R<PreciousMetalOrder> getOrder(@PathVariable String orderNo) {
        return R.ok(metalAppService.getOrder(orderNo));
    }

    @Operation(summary = "分页查询订单")
    @PostMapping("/order/page")
    public R<List<PreciousMetalOrder>> pageOrders(@Valid @RequestBody OrderQuery query) {
        return R.ok(metalAppService.pageOrders(query));
    }

    @Operation(summary = "市价重估")
    @PostMapping("/position/mark-to-market")
    @RequirePermission("precious-metal:position:mtm")
    public R<Void> markToMarket(@RequestParam Long customerId,
                                 @RequestParam String metalType,
                                 @RequestParam java.math.BigDecimal price) {
        metalAppService.markToMarket(customerId, metalType, price);
        return R.ok();
    }
}
