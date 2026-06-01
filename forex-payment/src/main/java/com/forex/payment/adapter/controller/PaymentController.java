package com.forex.payment.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.payment.adapter.dto.AmlCheckReq;
import com.forex.payment.adapter.dto.CreatePaymentReq;
import com.forex.payment.adapter.dto.GpiUpdateReq;
import com.forex.payment.adapter.dto.PaymentResp;
import com.forex.payment.adapter.dto.SendPaymentReq;
import com.forex.payment.application.command.CreatePaymentCmd;
import com.forex.payment.application.command.SendPaymentCmd;
import com.forex.payment.domain.model.dto.PaymentQuery;
import com.forex.payment.application.service.PaymentAppService;
import com.forex.payment.domain.model.aggregate.CrossBorderPayment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "跨境支付")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentAppService paymentAppService;

    @Operation(summary = "创建汇出支付")
    @PostMapping("/outward")
    @Idempotent(key = "#req.customerId + '_outward_' + T(java.lang.System).currentTimeMillis()")
    public R<PaymentResp> createOutwardPayment(@Valid @RequestBody CreatePaymentReq req) {
        CreatePaymentCmd cmd = toCmd(req);
        CrossBorderPayment payment = paymentAppService.createOutwardPayment(cmd);
        return R.ok("汇出支付创建成功", toResp(payment));
    }

    @Operation(summary = "创建汇入支付")
    @PostMapping("/inward")
    @Idempotent(key = "#req.customerId + '_inward_' + T(java.lang.System).currentTimeMillis()")
    public R<PaymentResp> createInwardPayment(@Valid @RequestBody CreatePaymentReq req) {
        CreatePaymentCmd cmd = toCmd(req);
        CrossBorderPayment payment = paymentAppService.createInwardPayment(cmd);
        return R.ok("汇入支付创建成功", toResp(payment));
    }

    @Operation(summary = "查询支付详情")
    @GetMapping("/{paymentNo}")
    public R<PaymentResp> getPaymentDetail(@PathVariable String paymentNo) {
        CrossBorderPayment payment = paymentAppService.getPaymentDetail(paymentNo);
        return R.ok(toResp(payment));
    }

    @Operation(summary = "分页查询支付")
    @PostMapping("/page")
    public R<PageResp<PaymentResp>> pageQuery(@RequestBody PaymentQuery query) {
        PageResp<CrossBorderPayment> page = paymentAppService.pageQuery(query);
        List<PaymentResp> respList = page.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<PaymentResp> result = PageResp.of(
                page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "提交支付")
    @PostMapping("/submit/{paymentNo}")
    @RedisLock(key = "#paymentNo")
    public R<PaymentResp> submitPayment(@PathVariable String paymentNo) {
        paymentAppService.submitPayment(paymentNo);
        CrossBorderPayment payment = paymentAppService.getPaymentDetail(paymentNo);
        return R.ok("支付已提交", toResp(payment));
    }

    @Operation(summary = "审批支付")
    @PostMapping("/approve/{paymentNo}")
    @RedisLock(key = "#paymentNo")
    public R<PaymentResp> approvePayment(@PathVariable String paymentNo) {
        paymentAppService.approvePayment(paymentNo);
        CrossBorderPayment payment = paymentAppService.getPaymentDetail(paymentNo);
        return R.ok("支付已审批", toResp(payment));
    }

    @Operation(summary = "反洗钱检查处理")
    @PostMapping("/aml-check")
    @RedisLock(key = "#req.paymentNo")
    public R<Void> processAmlCheck(@Valid @RequestBody AmlCheckReq req) {
        paymentAppService.processAmlCheck(req.getPaymentNo(), req.getPassed(), req.getReason());
        return R.okMsg("反洗钱检查已处理");
    }

    @Operation(summary = "发送支付")
    @PostMapping("/send")
    @RedisLock(key = "#req.paymentNo")
    @Idempotent(key = "#req.paymentNo + '_send'")
    public R<PaymentResp> sendPayment(@Valid @RequestBody SendPaymentReq req) {
        SendPaymentCmd cmd = new SendPaymentCmd();
        cmd.setPaymentNo(req.getPaymentNo());
        cmd.setSwiftRef(req.getSwiftRef());
        cmd.setCipsRef(req.getCipsRef());
        CrossBorderPayment payment = paymentAppService.sendPayment(
                req.getPaymentNo(), req.getSwiftRef(), req.getCipsRef());
        return R.ok("支付已发送", toResp(payment));
    }

    @Operation(summary = "取消支付")
    @PostMapping("/cancel/{paymentNo}")
    @RedisLock(key = "#paymentNo")
    public R<Void> cancelPayment(@PathVariable String paymentNo, @RequestParam String reason) {
        paymentAppService.cancelPayment(paymentNo, reason);
        return R.okMsg("支付已取消");
    }

    @Operation(summary = "更新GPI状态")
    @PutMapping("/gpi-status")
    public R<PaymentResp> updateGpiStatus(@Valid @RequestBody GpiUpdateReq req) {
        CrossBorderPayment payment = paymentAppService.updateGpiStatus(
                req.getPaymentNo(), req.getGpiStatus(), req.getTrackingId());
        return R.ok("GPI状态已更新", toResp(payment));
    }

    private CreatePaymentCmd toCmd(CreatePaymentReq req) {
        CreatePaymentCmd cmd = new CreatePaymentCmd();
        cmd.setCustomerId(req.getCustomerId());
        cmd.setPaymentType(req.getPaymentType());
        cmd.setPayAmount(req.getPayAmount());
        cmd.setPayCurrency(req.getPayCurrency());
        cmd.setBeneficiaryName(req.getBeneficiaryName());
        cmd.setBeneficiaryAccount(req.getBeneficiaryAccount());
        cmd.setBeneficiaryBank(req.getBeneficiaryBank());
        cmd.setBeneficiarySwift(req.getBeneficiarySwift());
        cmd.setBeneficiaryAddress(req.getBeneficiaryAddress());
        cmd.setBeneficiaryCountry(req.getBeneficiaryCountry());
        cmd.setSenderName(req.getSenderName());
        cmd.setSenderAccount(req.getSenderAccount());
        cmd.setSenderAddress(req.getSenderAddress());
        cmd.setIntermediaryBank(req.getIntermediaryBank());
        cmd.setPayingBankCode(req.getPayingBankCode());
        cmd.setReceivingBankCode(req.getReceivingBankCode());
        cmd.setPaymentPurpose(req.getPaymentPurpose());
        cmd.setBankPurposeCode(req.getBankPurposeCode());
        cmd.setChargeBearer(req.getChargeBearer());
        cmd.setValueDate(req.getValueDate());
        cmd.setRemark(req.getRemark());
        return cmd;
    }

    private PaymentResp toResp(CrossBorderPayment payment) {
        PaymentResp resp = new PaymentResp();
        resp.setId(payment.getId());
        resp.setPaymentNo(payment.getPaymentNo());
        resp.setCustomerId(payment.getCustomerId());
        resp.setPaymentDirection(payment.getPaymentDirection());
        resp.setPaymentType(payment.getPaymentType());
        resp.setPayAmount(payment.getPayAmount());
        resp.setPayCurrency(payment.getPayCurrency());
        resp.setSettlementAmount(payment.getSettlementAmount());
        resp.setExchangeRate(payment.getExchangeRate());
        resp.setSenderInfo(payment.getSenderInfo());
        resp.setBeneficiaryInfo(payment.getBeneficiaryInfo());
        resp.setIntermediaryBankInfo(payment.getIntermediaryBankInfo());
        resp.setPayingBankCode(payment.getPayingBankCode());
        resp.setReceivingBankCode(payment.getReceivingBankCode());
        resp.setMessageType(payment.getMessageType());
        resp.setSwiftRef(payment.getSwiftRef());
        resp.setCipsRef(payment.getCipsRef());
        resp.setGpiTrackingId(payment.getGpiTrackingId());
        resp.setGpiStatus(payment.getGpiStatus());
        resp.setPaymentPurpose(payment.getPaymentPurpose());
        resp.setBankPurposeCode(payment.getBankPurposeCode());
        resp.setChargeBearer(payment.getChargeBearer());
        resp.setFeeAmount(payment.getFeeAmount());
        resp.setTelegraphicFee(payment.getTelegraphicFee());
        resp.setCommissionAmount(payment.getCommissionAmount());
        resp.setPaymentStatus(payment.getPaymentStatus());
        resp.setSubmitTime(payment.getSubmitTime());
        resp.setValueDate(payment.getValueDate());
        resp.setSettlementDate(payment.getSettlementDate());
        resp.setOperatorId(payment.getOperatorId());
        resp.setApproverId(payment.getApproverId());
        resp.setRemark(payment.getRemark());
        resp.setCreateTime(payment.getCreatedAt());
        return resp;
    }
}
