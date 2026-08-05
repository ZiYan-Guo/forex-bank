package com.forex.settlement.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.settlement.adapter.dto.GuaranteeReq;
import com.forex.settlement.adapter.dto.GuaranteePageQuery;
import com.forex.settlement.adapter.dto.GuaranteeResp;
import com.forex.settlement.application.command.CreateGuaranteeCmd;
import com.forex.settlement.application.service.SettlementAppService;
import com.forex.settlement.domain.model.entity.BankGuarantee;
import com.forex.settlement.domain.model.query.GuaranteeQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.forex.common.security.annotation.RequirePermission;

import java.util.List;

@Tag(name = "国际保函")
@RestController
@RequestMapping("/api/settlement/guarantee")
@RequiredArgsConstructor
@Slf4j
public class GuaranteeController {

    private final SettlementAppService settlementAppService;

    @Operation(summary = "创建保函")
    @RequirePermission("settlement:create")
    @PostMapping("/create")
    @Idempotent(key = "#req.customerId + '_guar'")
    public R<GuaranteeResp> create(@Valid @RequestBody GuaranteeReq req) {
        log.info(
                "Create bank guarantee / 创建保函, customerId={}, currency={}, amount={}",
                req.getCustomerId(), req.getGuaranteeCurrency(), req.getGuaranteeAmount());
        CreateGuaranteeCmd cmd = toCmd(req);
        BankGuarantee guarantee = settlementAppService.createGuarantee(cmd);
        log.info("Bank guarantee created / 保函创建成功, guaranteeNo={}", guarantee.getGuaranteeNo());
        return R.ok(toResp(guarantee));
    }

    @Operation(summary = "查询保函详情")
    @GetMapping("/{guaranteeNo}")
    public R<GuaranteeResp> getDetail(@PathVariable String guaranteeNo) {
        BankGuarantee guarantee = settlementAppService.getGuaranteeDetail(guaranteeNo);
        return R.ok(toResp(guarantee));
    }

    @Operation(summary = "分页查询保函")
    @PostMapping("/page")
    public R<PageResp<GuaranteeResp>> pageQuery(@RequestBody GuaranteePageQuery req) {
        GuaranteeQuery query = toQuery(req);
        PageResp<BankGuarantee> page = settlementAppService.pageGuarantees(query);
        List<GuaranteeResp> records = page.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<GuaranteeResp> result = PageResp.of(
                page.getTotal(), records, page.getPageNum(), page.getPageSize());
        log.info(
                "Guarantee page response ready / 保函分页响应就绪, pageNum={}, pageSize={}, total={}",
                page.getPageNum(), page.getPageSize(), page.getTotal());
        return R.ok(result);
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

    /**
     * Maps the inbound DTO to an application command.
     * 将入站 DTO 转换为应用层命令，避免适配层对象泄漏到领域层。
     */
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

    /**
     * Builds guarantee query in adapter layer.
     * 在适配层构造保函查询对象，避免接口 DTO 泄漏到领域仓储端口。
     */
    private GuaranteeQuery toQuery(GuaranteePageQuery req) {
        GuaranteeQuery query = new GuaranteeQuery();
        query.setPageNum(req.getPageNum());
        query.setPageSize(req.getPageSize());
        query.setGuaranteeNo(req.getGuaranteeNo());
        query.setCustomerId(req.getCustomerId());
        query.setGuaranteeStatus(req.getGuaranteeStatus());
        return query;
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
