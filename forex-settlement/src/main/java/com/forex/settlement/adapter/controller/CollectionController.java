package com.forex.settlement.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.result.R;
import com.forex.settlement.adapter.dto.CollectionReq;
import com.forex.settlement.adapter.dto.CollectionResp;
import com.forex.settlement.application.command.CreateCollectionCmd;
import com.forex.settlement.application.service.SettlementAppService;
import com.forex.settlement.domain.model.entity.DocumentaryCollection;

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

@Tag(name = "跟单托收")
@RestController
@RequestMapping("/api/settlement/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final SettlementAppService settlementAppService;

    @Operation(summary = "创建托收")
    @PostMapping("/create")
    @Idempotent(key = "#req.customerId + '_col_' + T(java.lang.System).currentTimeMillis()")
    public R<CollectionResp> create(@Valid @RequestBody CollectionReq req) {
        CreateCollectionCmd cmd = toCmd(req);
        DocumentaryCollection col = settlementAppService.createCollection(cmd);
        return R.ok(toResp(col));
    }

    @Operation(summary = "查询托收详情")
    @GetMapping("/{collectionNo}")
    public R<CollectionResp> getDetail(@PathVariable String collectionNo) {
        DocumentaryCollection col = settlementAppService.getCollectionDetail(collectionNo);
        return R.ok(toResp(col));
    }

    @Operation(summary = "托收付款")
    @PostMapping("/pay/{collectionNo}")
    @RedisLock(key = "#collectionNo")
    public R<CollectionResp> pay(@PathVariable String collectionNo) {
        settlementAppService.payCollection(collectionNo);
        DocumentaryCollection col = settlementAppService.getCollectionDetail(collectionNo);
        return R.ok("托收已付款", toResp(col));
    }

    private CreateCollectionCmd toCmd(CollectionReq req) {
        CreateCollectionCmd cmd = new CreateCollectionCmd();
        cmd.setCustomerId(req.getCustomerId());
        cmd.setCollectionType(req.getCollectionType());
        cmd.setCollectionForm(req.getCollectionForm());
        cmd.setCollectionAmount(req.getCollectionAmount());
        cmd.setCollectionCurrency(req.getCollectionCurrency());
        cmd.setDraweeName(req.getDraweeName());
        cmd.setDocumentsList(req.getDocumentsList());
        cmd.setRemark(req.getRemark());
        return cmd;
    }

    private CollectionResp toResp(DocumentaryCollection col) {
        CollectionResp resp = new CollectionResp();
        resp.setId(col.getId());
        resp.setCollectionNo(col.getCollectionNo());
        resp.setCustomerId(col.getCustomerId());
        resp.setCollectionType(col.getCollectionType());
        resp.setCollectionForm(col.getCollectionForm());
        resp.setCollectionAmount(col.getCollectionAmount());
        resp.setCollectionCurrency(col.getCollectionCurrency());
        resp.setDrawerInfo(col.getDrawerInfo());
        resp.setDraweeInfo(col.getDraweeInfo());
        resp.setRemittingBank(col.getRemittingBank());
        resp.setCollectingBank(col.getCollectingBank());
        resp.setDocumentsList(col.getDocumentsList());
        resp.setCollectionStatus(col.getCollectionStatus());
        resp.setSwiftRef(col.getSwiftRef());
        resp.setOperatorId(col.getOperatorId());
        resp.setRemark(col.getRemark());
        return resp;
    }
}
