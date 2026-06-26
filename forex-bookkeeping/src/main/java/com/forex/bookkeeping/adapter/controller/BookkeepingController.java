package com.forex.bookkeeping.adapter.controller;

import com.forex.bookkeeping.adapter.dto.EntryResp;
import com.forex.bookkeeping.application.command.CreateEntryCmd;
import com.forex.bookkeeping.application.service.BookkeepingAppService;
import com.forex.bookkeeping.domain.model.aggregate.JournalEntry;
import com.forex.bookkeeping.domain.model.aggregate.MonthEndClosing;
import com.forex.bookkeeping.adapter.dto.JournalPageQuery;
import com.forex.bookkeeping.domain.model.query.JournalQuery;
import com.forex.bookkeeping.domain.service.MonthEndClosingService;
import com.forex.bookkeeping.domain.service.RevaluationEntryService;
import com.forex.common.base.annotation.Idempotent;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.result.R;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.forex.common.security.annotation.RequirePermission;
import com.forex.bookkeeping.adapter.dto.RevaluationReq;

@Tag(name = "簿记核算")
@RestController
@RequestMapping("/api/bookkeeping")
@RequiredArgsConstructor
public class BookkeepingController {

    private final BookkeepingAppService bookkeepingAppService;
    private final RevaluationEntryService revaluationEntryService;
    private final MonthEndClosingService monthEndClosingService;

    @Operation(summary = "创建记账分录")
    @RequirePermission("bookkeeping:create")
    @PostMapping("/entry/create")
    @Idempotent(key = "#cmd.bizNo + '_entry_create'")
    public R<EntryResp> createEntry(@Valid @RequestBody CreateEntryCmd cmd) {
        JournalEntry entry = bookkeepingAppService.createJournalEntry(cmd);
        return R.ok("创建成功", toEntryResp(entry));
    }

    @Operation(summary = "过账")
    @RequirePermission("bookkeeping:post")
    @PostMapping("/entry/post/{voucherNo}")
    @RedisLock(key = "#voucherNo")
    public R<Void> postEntry(@PathVariable String voucherNo) {
        bookkeepingAppService.postEntry(voucherNo);
        return R.okMsg("过账成功");
    }

    @Operation(summary = "冲正")
    @RequirePermission("bookkeeping:reverse")
    @PostMapping("/entry/reverse/{voucherNo}")
    @RedisLock(key = "#voucherNo")
    public R<EntryResp> reverseEntry(@PathVariable String voucherNo,
                                      @RequestBody(required = false) String reason) {
        JournalEntry reversal = bookkeepingAppService.reverseEntry(voucherNo, reason);
        return R.ok("冲正成功", toEntryResp(reversal));
    }

    @Operation(summary = "查询记账分录详情")
    @GetMapping("/entry/{voucherNo}")
    public R<EntryResp> getEntry(@PathVariable String voucherNo) {
        JournalEntry entry = bookkeepingAppService.getEntryDetail(voucherNo);
        return R.ok(toEntryResp(entry));
    }

    @Operation(summary = "分页查询记账分录")
    @RequirePermission("bookkeeping:page")
    @PostMapping("/entry/page")
    public R<PageResp<EntryResp>> pageQuery(@Valid @RequestBody JournalPageQuery req) {
        JournalQuery query = new JournalQuery();
        query.setPageNum(req.getPageNum());
        query.setPageSize(req.getPageSize());
        query.setVoucherNo(req.getVoucherNo());
        query.setVoucherDate(req.getVoucherDate());
        query.setFiscalPeriod(req.getFiscalPeriod());
        query.setBizType(req.getBizType());
        query.setEntryStatus(req.getEntryStatus());
        query.setAccountCode(req.getAccountCode());
        query.setEntryDirection(req.getEntryDirection());
        query.setCurrency(req.getCurrency());
        query.setStartDate(req.getStartDate());
        query.setEndDate(req.getEndDate());
        PageResp<JournalEntry> page = bookkeepingAppService.pageQuery(query);
        List<EntryResp> respList = page.getRecords().stream()
                .map(this::toEntryResp)
                .toList();
        PageResp<EntryResp> result = PageResp.of(
                page.getTotal(), respList, page.getPageNum(), page.getPageSize());
        return R.ok(result);
    }

    @Operation(summary = "生成外币重估分录")
    @RequirePermission("bookkeeping:revaluation")
    @PostMapping("/revaluation")
    public R<List<EntryResp>> revaluation(@RequestBody RevaluationReq request) {
        List<RevaluationReq.CurrencyBalance> currencies = request.getCurrencies();
        List<RevaluationEntryService.FxBalance> balances = currencies.stream().map(m -> {
            RevaluationEntryService.FxBalance b = new RevaluationEntryService.FxBalance();
            b.setCurrency(m.getCurrency());
            b.setOldRate(m.getOldRate());
            b.setNewRate(m.getNewRate());
            b.setBalance(m.getBalance());
            return b;
        }).toList();
        List<JournalEntry> entries = revaluationEntryService.batchRevaluation(balances);
        List<EntryResp> respList = entries.stream().map(this::toEntryResp).toList();
        return R.ok("重估分录生成成功", respList);
    }

    @Operation(summary = "月末结账")
    @RequirePermission("bookkeeping:month-end")
    @PostMapping("/closing/month-end/{fiscalPeriod}")
    public R<MonthEndClosing> monthEndClosing(@PathVariable String fiscalPeriod,
                                               @RequestBody(required = false) List<RevaluationEntryService.FxBalance> balances) {
        MonthEndClosing closing = monthEndClosingService.executeMonthEndClosing(fiscalPeriod, balances);
        return R.ok("月末结账完成", closing);
    }

    @Operation(summary = "日终批量过账")
    @RequirePermission("bookkeeping:closing")
    @PostMapping("/closing/{date}")
    public R<Void> dailyClosing(@PathVariable LocalDate date) {
        bookkeepingAppService.dailyClosing(date);
        return R.okMsg("日终过账完成");
    }

    private EntryResp toEntryResp(JournalEntry entry) {
        EntryResp resp = new EntryResp();
        resp.setId(entry.getId());
        resp.setVoucherNo(entry.getVoucherNo());
        resp.setVoucherDate(entry.getVoucherDate());
        resp.setFiscalPeriod(entry.getFiscalPeriod());
        resp.setBizType(entry.getBizType());
        resp.setBizNo(entry.getBizNo());
        resp.setCurrency(entry.getCurrency());
        resp.setAmount(entry.getAmount());
        resp.setEntryDirection(entry.getEntryDirection());
        resp.setAccountCode(entry.getAccountCode());
        resp.setAccountName(entry.getAccountName());
        resp.setOppositeAccountCode(entry.getOppositeAccountCode());
        resp.setSummary(entry.getSummary());
        resp.setEntryStatus(entry.getEntryStatus());
        resp.setReversedVoucherNo(entry.getReversedVoucherNo());
        resp.setPostedTime(entry.getPostedTime());
        resp.setOperatorId(entry.getOperatorId());
        return resp;
    }
}
