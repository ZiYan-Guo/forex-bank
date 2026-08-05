package com.forex.settlement.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.settlement.adapter.dto.CreateLcReq;
import com.forex.settlement.adapter.dto.LcPageQuery;
import com.forex.settlement.adapter.dto.LcResp;
import com.forex.settlement.application.command.AmendLcCmd;
import com.forex.settlement.application.command.CreateLcCmd;
import com.forex.settlement.application.service.SettlementAppService;
import com.forex.settlement.domain.model.aggregate.LetterOfCredit;
import com.forex.settlement.domain.model.query.LcQuery;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "信用证管理")
@RestController
@RequestMapping("/api/settlement/lc")
@RequiredArgsConstructor
@Slf4j
public class LcController {

    private final SettlementAppService settlementAppService;

    @Operation(summary = "创建信用证")
    @PostMapping("/create")
    @RequirePermission("settlement:lc:create")
    @Idempotent(key = "#req.customerId + '_lc'")
    public R<LcResp> createLc(@Valid @RequestBody CreateLcReq req) {
        log.info(
                "Create letter of credit / 创建信用证, customerId={}, currency={}, amount={}",
                req.getCustomerId(), req.getLcCurrency(), req.getLcAmount());
        CreateLcCmd cmd = toCmd(req);
        LetterOfCredit lc = settlementAppService.createLc(cmd);
        log.info("Letter of credit created / 信用证创建成功, lcNo={}", lc.getLcNo());
        return R.ok(toResp(lc));
    }

    @Operation(summary = "查询信用证详情")
    @GetMapping("/{lcNo}")
    public R<LcResp> getDetail(@PathVariable String lcNo) {
        LetterOfCredit lc = settlementAppService.getLcDetail(lcNo);
        return R.ok(toResp(lc));
    }

    @Operation(summary = "分页查询信用证")
    @PostMapping("/page")
    public R<PageResp<LcResp>> pageQuery(@RequestBody LcPageQuery req) {
        LcQuery query = toLcQuery(req);
        PageResp<LetterOfCredit> page = settlementAppService.pageQuery(query);
        List<LcResp> respList = page.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<LcResp> result = PageResp.of(page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "开立信用证")
    @PostMapping("/issue/{lcNo}")
    @RequirePermission("settlement:lc:issue")
    @RedisLock(key = "#lcNo")
    public R<LcResp> issue(@PathVariable String lcNo) {
        settlementAppService.issueLc(lcNo);
        LetterOfCredit lc = settlementAppService.getLcDetail(lcNo);
        return R.ok("信用证已开立", toResp(lc));
    }

    @Operation(summary = "修改信用证")
    @PostMapping("/amend")
    @RequirePermission("settlement:lc:amend")
    @RedisLock(key = "#cmd.lcNo")
    public R<LcResp> amend(@Valid @RequestBody AmendLcCmd cmd) {
        settlementAppService.amendLc(cmd.getLcNo(), cmd);
        LetterOfCredit lc = settlementAppService.getLcDetail(cmd.getLcNo());
        return R.ok("信用证已修改", toResp(lc));
    }

    @Operation(summary = "交单")
    @PostMapping("/present/{lcNo}")
    @RequirePermission("settlement:lc:present")
    @RedisLock(key = "#lcNo")
    public R<LcResp> presentDocs(@PathVariable String lcNo) {
        settlementAppService.presentDocuments(lcNo);
        LetterOfCredit lc = settlementAppService.getLcDetail(lcNo);
        return R.ok("信用证已交单", toResp(lc));
    }

    @Operation(summary = "审单")
    @PostMapping("/check-docs")
    @RequirePermission("settlement:lc:check")
    @RedisLock(key = "#lcNo")
    public R<LcResp> checkDocs(@RequestParam String lcNo, @RequestParam boolean discrepant) {
        settlementAppService.checkDocuments(lcNo, discrepant);
        LetterOfCredit lc = settlementAppService.getLcDetail(lcNo);
        return R.ok("审单完成", toResp(lc));
    }

    @Operation(summary = "承兑")
    @PostMapping("/accept/{lcNo}")
    @RequirePermission("settlement:lc:accept")
    @RedisLock(key = "#lcNo")
    public R<LcResp> accept(@PathVariable String lcNo) {
        settlementAppService.acceptLc(lcNo);
        LetterOfCredit lc = settlementAppService.getLcDetail(lcNo);
        return R.ok("信用证已承兑", toResp(lc));
    }

    @Operation(summary = "付款")
    @PostMapping("/pay/{lcNo}")
    @RequirePermission("settlement:lc:pay")
    @RedisLock(key = "#lcNo")
    public R<LcResp> pay(@PathVariable String lcNo) {
        settlementAppService.payLc(lcNo);
        LetterOfCredit lc = settlementAppService.getLcDetail(lcNo);
        return R.ok("信用证已付款", toResp(lc));
    }

    /**
     * Builds a query object in the adapter layer.
     * 在适配层构造查询对象，保持查询参数与领域模型隔离。
     */
    private LcQuery toLcQuery(LcPageQuery req) {
        LcQuery query = new LcQuery();
        query.setPageNum(req.getPageNum());
        query.setPageSize(req.getPageSize());
        query.setLcNo(req.getLcNo());
        query.setCustomerId(req.getCustomerId());
        query.setLcType(req.getLcType());
        query.setLcStatus(req.getLcStatus());
        query.setStartDate(req.getStartDate());
        query.setEndDate(req.getEndDate());
        return query;
    }

    /**
     * Maps the inbound DTO to an application command.
     * 将入站 DTO 转换为应用层命令，避免适配层对象泄漏到领域层。
     */
    private CreateLcCmd toCmd(CreateLcReq req) {
        CreateLcCmd cmd = new CreateLcCmd();
        cmd.setCustomerId(req.getCustomerId());
        cmd.setLcType(req.getLcType());
        cmd.setLcDirection(req.getLcDirection());
        cmd.setLcAmount(req.getLcAmount());
        cmd.setLcCurrency(req.getLcCurrency());
        cmd.setApplicantName(req.getApplicantName());
        cmd.setApplicantAddress(req.getApplicantAddress());
        cmd.setBeneficiaryName(req.getBeneficiaryName());
        cmd.setBeneficiaryAccount(req.getBeneficiaryAccount());
        cmd.setBeneficiaryBank(req.getBeneficiaryBank());
        cmd.setIssuingBank(req.getIssuingBank());
        cmd.setAdvisingBank(req.getAdvisingBank());
        cmd.setIssueDate(req.getIssueDate());
        cmd.setExpiryDate(req.getExpiryDate());
        cmd.setAvailableBy(req.getAvailableBy());
        cmd.setGoodsDescription(req.getGoodsDescription());
        cmd.setDocumentsRequired(req.getDocumentsRequired());
        cmd.setMarginPct(req.getMarginPct());
        cmd.setFeeAmount(req.getFeeAmount());
        cmd.setRemark(req.getRemark());
        return cmd;
    }

    private LcResp toResp(LetterOfCredit lc) {
        LcResp resp = new LcResp();
        resp.setId(lc.getId());
        resp.setLcNo(lc.getLcNo());
        resp.setCustomerId(lc.getCustomerId());
        resp.setLcType(lc.getLcType());
        resp.setLcDirection(lc.getLcDirection());
        resp.setLcAmount(lc.getLcAmount());
        resp.setLcCurrency(lc.getLcCurrency());
        resp.setTolerancePct(lc.getTolerancePct());
        resp.setApplicantInfo(lc.getApplicantInfo());
        resp.setBeneficiaryInfo(lc.getBeneficiaryInfo());
        resp.setIssuingBankInfo(lc.getIssuingBankInfo());
        resp.setAdvisingBankInfo(lc.getAdvisingBankInfo());
        resp.setConfirmingBankInfo(lc.getConfirmingBankInfo());
        resp.setIssueDate(lc.getIssueDate());
        resp.setExpiryDate(lc.getExpiryDate());
        resp.setExpiryPlace(lc.getExpiryPlace());
        resp.setLatestShipDate(lc.getLatestShipDate());
        resp.setPresentationPeriod(lc.getPresentationPeriod());
        resp.setAvailableWith(lc.getAvailableWith());
        resp.setAvailableBy(lc.getAvailableBy());
        resp.setDraftTenor(lc.getDraftTenor());
        resp.setPartialShipment(lc.getPartialShipment());
        resp.setTransshipment(lc.getTransshipment());
        resp.setPortOfLoading(lc.getPortOfLoading());
        resp.setPortOfDischarge(lc.getPortOfDischarge());
        resp.setGoodsDescription(lc.getGoodsDescription());
        resp.setDocumentsRequired(lc.getDocumentsRequired());
        resp.setAdditionalConditions(lc.getAdditionalConditions());
        resp.setConfirmationInstruction(lc.getConfirmationInstruction());
        resp.setChargeBearer(lc.getChargeBearer());
        resp.setLcStatus(lc.getLcStatus());
        resp.setSwiftRef(lc.getSwiftRef());
        resp.setMarginPct(lc.getMarginPct());
        resp.setMarginAmount(lc.getMarginAmount());
        resp.setFeeAmount(lc.getFeeAmount());
        resp.setOperatorId(lc.getOperatorId());
        resp.setRemark(lc.getRemark());
        resp.setCreateTime(lc.getCreatedAt());
        return resp;
    }
}
