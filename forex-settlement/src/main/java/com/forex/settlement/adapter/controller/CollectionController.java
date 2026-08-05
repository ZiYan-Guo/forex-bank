package com.forex.settlement.adapter.controller;

import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;
import com.forex.settlement.adapter.dto.CollectionPageQuery;
import com.forex.settlement.adapter.dto.CollectionReq;
import com.forex.settlement.adapter.dto.CollectionResp;
import com.forex.settlement.application.command.CreateCollectionCmd;
import com.forex.settlement.application.service.SettlementAppService;
import com.forex.settlement.domain.model.entity.DocumentaryCollection;
import com.forex.settlement.domain.model.query.CollectionQuery;

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

@Tag(name = "跟单托收")
@RestController
@RequestMapping("/api/settlement/collection")
@RequiredArgsConstructor
@Slf4j
public class CollectionController {

    private final SettlementAppService settlementAppService;

    @Operation(summary = "创建托收")
    @RequirePermission("settlement:create")
    @PostMapping("/create")
    @Idempotent(key = "#req.customerId + '_col'")
    public R<CollectionResp> create(@Valid @RequestBody CollectionReq req) {
        log.info(
                "Create documentary collection / 创建跟单托收, customerId={}, currency={}, amount={}",
                req.getCustomerId(), req.getCollectionCurrency(), req.getCollectionAmount());
        CreateCollectionCmd cmd = toCmd(req);
        DocumentaryCollection col = settlementAppService.createCollection(cmd);
        log.info("Documentary collection created / 跟单托收创建成功, collectionNo={}", col.getCollectionNo());
        return R.ok(toResp(col));
    }

    @Operation(summary = "查询托收详情")
    @GetMapping("/{collectionNo}")
    public R<CollectionResp> getDetail(@PathVariable String collectionNo) {
        DocumentaryCollection col = settlementAppService.getCollectionDetail(collectionNo);
        return R.ok(toResp(col));
    }

    @Operation(summary = "分页查询托收")
    @PostMapping("/page")
    public R<PageResp<CollectionResp>> pageQuery(@RequestBody CollectionPageQuery req) {
        CollectionQuery query = toQuery(req);
        PageResp<DocumentaryCollection> page = settlementAppService.pageCollections(query);
        List<CollectionResp> records = page.getRecords().stream()
                .map(this::toResp)
                .toList();
        PageResp<CollectionResp> result = PageResp.of(
                page.getTotal(), records, page.getPageNum(), page.getPageSize());
        log.info(
                "Collection page response ready / 托收分页响应就绪, pageNum={}, pageSize={}, total={}",
                page.getPageNum(), page.getPageSize(), page.getTotal());
        return R.ok(result);
    }

    @Operation(summary = "托收付款")
    @RequirePermission("settlement:pay")
    @PostMapping("/pay/{collectionNo}")
    @RedisLock(key = "#collectionNo")
    public R<CollectionResp> pay(@PathVariable String collectionNo) {
        settlementAppService.payCollection(collectionNo);
        DocumentaryCollection col = settlementAppService.getCollectionDetail(collectionNo);
        return R.ok("托收已付款", toResp(col));
    }

    /**
     * Maps the inbound DTO to an application command.
     * 将入站 DTO 转换为应用层命令，避免适配层对象泄漏到领域层。
     */
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

    /**
     * Builds collection query in adapter layer.
     * 在适配层构造托收查询对象，避免接口 DTO 泄漏到领域仓储端口。
     */
    private CollectionQuery toQuery(CollectionPageQuery req) {
        CollectionQuery query = new CollectionQuery();
        query.setPageNum(req.getPageNum());
        query.setPageSize(req.getPageSize());
        query.setCollectionNo(req.getCollectionNo());
        query.setCustomerId(req.getCustomerId());
        query.setCollectionStatus(req.getCollectionStatus());
        return query;
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
