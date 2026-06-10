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
import com.forex.payment.application.service.BankCodeValidationService;
import com.forex.payment.application.service.BatchPaymentService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "跨境支付")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentAppService paymentAppService;
    private final BatchPaymentService batchPaymentService;
    private final BankCodeValidationService bankCodeValidationService;

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

    @Operation(summary = "批量提交支付")
    @PostMapping("/batch/submit")
    public R<Map<String, Object>> batchSubmit(@RequestBody Map<String, Object> body) {
        String channel = (String) body.get("channel");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> payments = (List<Map<String, Object>>) body.get("payments");
        List<CreatePaymentCmd> commands = new ArrayList<>();
        for (Map<String, Object> p : payments) {
            CreatePaymentCmd cmd = new CreatePaymentCmd();
            cmd.setCustomerId(p.get("customerId") != null
                    ? Long.valueOf(p.get("customerId").toString()) : null);
            cmd.setPaymentType((String) p.get("paymentType"));
            cmd.setPayAmount(p.get("payAmount") != null
                    ? new BigDecimal(p.get("payAmount").toString()) : null);
            cmd.setPayCurrency((String) p.get("payCurrency"));
            cmd.setBeneficiaryName((String) p.get("beneficiaryName"));
            cmd.setBeneficiaryAccount((String) p.get("beneficiaryAccount"));
            cmd.setBeneficiaryBank((String) p.get("beneficiaryBank"));
            cmd.setBeneficiarySwift((String) p.get("beneficiarySwift"));
            cmd.setBeneficiaryCountry((String) p.get("beneficiaryCountry"));
            cmd.setReceivingBankCode((String) p.get("receivingBankCode"));
            cmd.setPaymentPurpose((String) p.get("paymentPurpose"));
            cmd.setChargeBearer((String) p.get("chargeBearer"));
            commands.add(cmd);
        }
        return R.ok(Map.of("channel", channel, "successCount",
                batchPaymentService.processBatch(commands, channel).getSuccessCount()));
    }

    @Operation(summary = "验证SWIFT/BIC代码")
    @PostMapping("/validate/swift")
    public R<Map<String, Object>> validateSwift(@RequestBody Map<String, String> body) {
        String swiftCode = body.get("swiftCode");
        boolean valid = bankCodeValidationService.validateSwiftCode(swiftCode);
        String completed = bankCodeValidationService.autoCompleteBic(swiftCode, null);
        return R.ok(Map.of("swiftCode", swiftCode, "valid", valid, "completed", completed != null ? completed : ""));
    }

    @Operation(summary = "验证IBAN")
    @PostMapping("/validate/iban")
    public R<Map<String, Object>> validateIban(@RequestBody Map<String, String> body) {
        String iban = body.get("iban");
        boolean valid = bankCodeValidationService.validateIban(iban);
        return R.ok(Map.of("iban", iban, "valid", valid));
    }

    @Operation(summary = "计算渠道费用")
    @PostMapping("/fee/calculate")
    public R<Map<String, Object>> calculateFee(@RequestBody Map<String, Object> body) {
        String channel = (String) body.get("channel");
        String chargeBearer = (String) body.get("chargeBearer");
        BigDecimal amount = body.get("amount") != null
                ? new BigDecimal(body.get("amount").toString()) : BigDecimal.ZERO;
        BigDecimal fee = bankCodeValidationService.calculateFee(channel, chargeBearer, amount);
        return R.ok(Map.of("channel", channel, "chargeBearer", chargeBearer, "amount", amount, "fee", fee));
    }

    @Operation(summary = "校验结构化地址")
    @PostMapping("/validate/address")
    public R<Map<String, Object>> validateAddress(@RequestBody Map<String, String> req) {
        List<String> missing = new ArrayList<>();
        if (isBlank(req.get("country"))) missing.add("country");
        if (isBlank(req.get("city"))) missing.add("city");
        if (isBlank(req.get("streetName")) && isBlank(req.get("buildingNumber"))) missing.add("streetOrBuilding");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("valid", missing.isEmpty());
        result.put("missingFields", missing);
        result.put("message", missing.isEmpty() ? "地址校验通过" : "缺少必要字段: " + String.join(", ", missing));
        return R.ok(result);
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }

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
