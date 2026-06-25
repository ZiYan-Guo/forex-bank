package com.forex.exchange.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.exchange.adapter.dto.AmountCalcReq;
import com.forex.exchange.adapter.dto.CreateOrderReq;
import com.forex.exchange.adapter.dto.ExchangeOrderPageQuery;
import com.forex.exchange.adapter.dto.OrderResp;
import com.forex.exchange.adapter.dto.QuoteReq;
import com.forex.exchange.adapter.dto.QuoteResp;
import com.forex.exchange.adapter.dto.RateLockReq;
import com.forex.exchange.application.command.CancelOrderCmd;
import com.forex.exchange.application.command.CreateOrderCmd;
import com.forex.exchange.domain.model.query.ExchangeOrderQuery;
import com.forex.exchange.application.service.ExchangeAppService;
import com.forex.exchange.domain.model.aggregate.ExchangeOrder;
import com.forex.exchange.domain.model.entity.ExchangeQuote;
import com.forex.exchange.domain.model.query.ExchangeOrderQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "结售汇业务")
@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeAppService exchangeAppService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    @RequirePermission("exchange:create")
    @Idempotent(key = "#req.customerId + '_' + #req.orderType + '_create'")
    public R<OrderResp> createOrder(@Valid @RequestBody CreateOrderReq req) {
        CreateOrderCmd cmd = toCreateCmd(req);
        ExchangeOrder order;
        if ("FORWARD".equals(req.getOrderType())) {
            order = exchangeAppService.createForwardOrder(cmd);
        } else if ("PENDING_ORDER".equals(req.getOrderType())) {
            order = exchangeAppService.createPendingOrder(cmd);
        } else {
            order = exchangeAppService.createSpotOrder(cmd);
        }
        return R.ok("创建成功", toOrderResp(order));
    }

    @Operation(summary = "锁汇")
    @PostMapping("/lock-rate")
    @RequirePermission("exchange:lock-rate")
    @RedisLock(key = "'exchange:lock:'+#req.orderNo")
    public R<OrderResp> lockRate(@Valid @RequestBody RateLockReq req) {
        ExchangeOrder order = exchangeAppService.lockRate(req.getOrderNo(), req.getConfirmedRate());
        return R.ok(toOrderResp(order));
    }

    @Operation(summary = "确认订单")
    @PostMapping("/confirm/{orderNo}")
    @RequirePermission("exchange:confirm")
    @RedisLock(key = "'exchange:confirm:'+#orderNo")
    public R<OrderResp> confirmOrder(@PathVariable String orderNo) {
        ExchangeOrder order = exchangeAppService.confirmOrder(orderNo);
        return R.ok("确认成功", toOrderResp(order));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel")
    @RequirePermission("exchange:cancel")
    @RedisLock(key = "'exchange:cancel:'+#cmd.orderNo")
    @Idempotent(key = "#cmd.orderNo + '_cancel'")
    public R<Void> cancelOrder(@Valid @RequestBody CancelOrderCmd cmd) {
        exchangeAppService.cancelOrder(cmd.getOrderNo(), cmd.getReason());
        return R.okMsg("订单已取消");
    }

    @Operation(summary = "冲正订单")
    @PostMapping("/reverse/{orderNo}")
    @RequirePermission("exchange:reverse")
    @RedisLock(key = "'exchange:reverse:'+#orderNo")
    public R<Void> reverseOrder(@PathVariable String orderNo) {
        exchangeAppService.reverseOrder(orderNo);
        return R.okMsg("冲正成功");
    }

    @Operation(summary = "查询订单详情")
    @GetMapping("/{orderNo}")
    public R<OrderResp> getOrderDetail(@PathVariable String orderNo) {
        ExchangeOrder order = exchangeAppService.getOrderDetail(orderNo);
        return R.ok(toOrderResp(order));
    }

    @Operation(summary = "分页查询订单")
    @PostMapping("/page")
    public R<PageResp<OrderResp>> pageQuery(@RequestBody ExchangeOrderPageQuery req) {
        ExchangeOrderQuery query = toExchangeQuery(req);
        PageResp<ExchangeOrder> page = exchangeAppService.pageQuery(query);
        List<OrderResp> respList = page.getRecords().stream()
                .map(this::toOrderResp)
                .toList();
        PageResp<OrderResp> result = PageResp.of(page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "获取报价")
    @PostMapping("/quote")
    public R<QuoteResp> getQuote(@Valid @RequestBody QuoteReq req) {
        ExchangeQuote quote = exchangeAppService.getQuote(
                req.getCustomerId(), req.getBaseCurrency(), req.getQuoteCurrency());
        if (quote == null) {
            return R.ok(new QuoteResp());
        }
        QuoteResp resp = toQuoteResp(quote);
        return R.ok(resp);
    }

    @Operation(summary = "计算金额")
    @PostMapping("/calculate")
    public R<BigDecimal> calculateAmount(@Valid @RequestBody AmountCalcReq req) {
        BigDecimal result = exchangeAppService.calculateAmount(
                req.getAmount(), req.getBaseCurrency(), req.getQuoteCurrency(), req.getDealType());
        return R.ok(result);
    }

    private ExchangeOrderQuery toExchangeQuery(ExchangeOrderPageQuery req) {
        ExchangeOrderQuery query = new ExchangeOrderQuery();
        query.setPageNum(req.getPageNum());
        query.setPageSize(req.getPageSize());
        query.setCustomerId(req.getCustomerId());
        query.setOrderNo(req.getOrderNo());
        query.setOrderType(req.getOrderType());
        query.setDealType(req.getDealType());
        query.setOrderStatus(req.getOrderStatus());
        query.setBaseCurrency(req.getBaseCurrency());
        query.setQuoteCurrency(req.getQuoteCurrency());
        return query;
    }

    private CreateOrderCmd toCreateCmd(CreateOrderReq req) {
        CreateOrderCmd cmd = new CreateOrderCmd();
        cmd.setCustomerId(req.getCustomerId());
        cmd.setOrderType(req.getOrderType());
        cmd.setDealType(req.getDealType());
        cmd.setBaseCurrency(req.getBaseCurrency());
        cmd.setQuoteCurrency(req.getQuoteCurrency());
        cmd.setOrderAmount(req.getOrderAmount());
        cmd.setRateType(req.getRateType());
        cmd.setMaturityDate(req.getMaturityDate());
        cmd.setSettlementType(req.getSettlementType());
        cmd.setChannel(req.getChannel());
        cmd.setCustomerAccountNo(req.getCustomerAccountNo());
        cmd.setRemark(req.getRemark());
        return cmd;
    }

    private OrderResp toOrderResp(ExchangeOrder order) {
        OrderResp resp = new OrderResp();
        resp.setId(order.getId());
        resp.setOrderNo(order.getOrderNo());
        resp.setCustomerId(order.getCustomerId());
        resp.setOrderType(order.getOrderType());
        resp.setDealType(order.getDealType());
        resp.setBaseCurrency(order.getBaseCurrency());
        resp.setQuoteCurrency(order.getQuoteCurrency());
        resp.setOrderAmount(order.getOrderAmount());
        resp.setSettleAmount(order.getSettleAmount());
        resp.setBidRate(order.getBidRate());
        resp.setAskRate(order.getAskRate());
        resp.setConfirmedRate(order.getConfirmedRate());
        resp.setRateType(order.getRateType());
        resp.setLockRateTime(order.getLockRateTime());
        resp.setLockRateExpireTime(order.getLockRateExpireTime());
        resp.setValueDate(order.getValueDate());
        resp.setMaturityDate(order.getMaturityDate());
        resp.setOrderStatus(order.getOrderStatus());
        resp.setCustomerAccountNo(order.getCustomerAccountNo());
        resp.setBankAccountNo(order.getBankAccountNo());
        resp.setFeeAmount(order.getFeeAmount());
        resp.setCommissionAmount(order.getCommissionAmount());
        resp.setSettlementType(order.getSettlementType());
        resp.setChannel(order.getChannel());
        resp.setOperatorId(order.getOperatorId());
        resp.setRemark(order.getRemark());
        return resp;
    }

    private QuoteResp toQuoteResp(ExchangeQuote quote) {
        QuoteResp resp = new QuoteResp();
        resp.setBidRate(quote.getBidRate());
        resp.setAskRate(quote.getAskRate());
        resp.setMidRate(quote.getMidRate());
        resp.setQuoteTime(quote.getQuoteTime());
        resp.setExpireTime(quote.getExpireTime());
        return resp;
    }
}
