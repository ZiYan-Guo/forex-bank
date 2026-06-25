package com.forex.settlement.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.result.R;
import com.forex.settlement.adapter.dto.GuaranteeReq;
import com.forex.settlement.adapter.dto.GuaranteeResp;
import com.forex.settlement.application.command.CreateGuaranteeCmd;
import com.forex.settlement.application.service.SettlementAppService;
import com.forex.settlement.domain.model.entity.BankGuarantee;

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
import com.forex.common.security.annotation.RequirePermission;

@Tag(name = "国际保函")
@RestController
@RequestMapping("/api/settlement/guarantee")
@RequiredArgsConstructor
public class GuaranteeController {

    private final SettlementAppService settlementAppService;

    @Operation(summary = "创建保函")
    @RequirePermission("settlement:create")
    @PostMapping("/create")
    @Idempotent(key = "#req.customerId + '_guar_' + T(java.lang.System).currentTimeMillis()")
    public R<GuaranteeResp> create(@Valid @RequestBody GuaranteeReq req) {
        CreateGuaranteeCmd cmd = toCmd(req);
        BankGuarantee guarantee = settlementAppService.createGuarantee(cmd);
        return R.ok(toResp(guarantee));
    }

    @Operation(summary = "查询保函详情")
    @GetMapping("/{guaranteeNo}")
    public R<GuaranteeResp> getDetail(@PathVariable String guaranteeNo) {
        BankGuarantee guarantee = settlementAppService.getGuaranteeDetail(guaranteeNo);
        return R.ok(toResp(guarantee));
    }

    @Operation(summary = "开立保函")
    @RequirePermission("settlement:issue")
    @PostMapping("/issue/{guaranteeNo}")
    @RedisLock(key = "#guaranteeNo")
    public R<GuaranteeResp> issue(@PathVariable String guaranteeNo) {
        settlementAppService.issueGuarantee(guaranteeNo);
        BankGuarantee guarantee = settlementAppService.getGuaranteeDetail(guaranteeNo);
        return R.ok("保函已开立", toResp(guarantee));
    }

    private CreateGuaranteeCmd toCmd(GuaranteeReq req) {
        CreateGuaranteeCmd cmd = new CreateGuaranteeCmd();
        cmd.setCustomerId(req.getCustomerId());
        cmd.setGuaranteeType(req.getGuaranteeType());
        cmd.setGuaranteeAmount(req.getGuaranteeAmount());
        cmd.setGuaranteeCurrency(req.getGuaranteeCurrency());
        cmd.setBeneficiaryName(req.getBeneficiaryName());
        cmd.setEffectiveDate(req.getEffectiveDate());
        cmd.setExpiryDate(req.getExpiryDate());
        cmd.setGuaranteeFormat(req.getGuaranteeFormat());
        cmd.setCommissionRate(req.getCommissionRate());
        cmd.setRemark(req.getRemark());
        return cmd;
    }

    private GuaranteeResp toResp(BankGuarantee guarantee) {
        GuaranteeResp resp = new GuaranteeResp();
        resp.setId(guarantee.getId());
        resp.setGuaranteeNo(guarantee.getGuaranteeNo());
        resp.setCustomerId(guarantee.getCustomerId());
        resp.setGuaranteeType(guarantee.getGuaranteeType());
        resp.setGuaranteeAmount(guarantee.getGuaranteeAmount());
        resp.setGuaranteeCurrency(guarantee.getGuaranteeCurrency());
        resp.setBeneficiaryInfo(guarantee.getBeneficiaryInfo());
        resp.setIssueDate(guarantee.getIssueDate());
        resp.setEffectiveDate(guarantee.getEffectiveDate());
        resp.setExpiryDate(guarantee.getExpiryDate());
        resp.setClaimExpiryDate(guarantee.getClaimExpiryDate());
        resp.setCounterGuaranteeNo(guarantee.getCounterGuaranteeNo());
        resp.setGuaranteeFormat(guarantee.getGuaranteeFormat());
        resp.setGuaranteeStatus(guarantee.getGuaranteeStatus());
        resp.setFeeAmount(guarantee.getFeeAmount());
        resp.setCommissionRate(guarantee.getCommissionRate());
        resp.setOperatorId(guarantee.getOperatorId());
        resp.setSwiftRef(guarantee.getSwiftRef());
        resp.setRemark(guarantee.getRemark());
        return resp;
    }
}
