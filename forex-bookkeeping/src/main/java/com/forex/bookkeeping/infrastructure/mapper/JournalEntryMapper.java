package com.forex.bookkeeping.infrastructure.mapper;

import com.forex.bookkeeping.domain.model.query.JournalQuery;
import com.forex.bookkeeping.infrastructure.persistence.JournalEntryPO;
import com.forex.common.mybatis.base.BaseMapperExt;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JournalEntryMapper extends BaseMapperExt<JournalEntryPO> {

    @Select("SELECT * FROM t_journal_entry WHERE voucher_no = #{voucherNo} AND deleted = 0")
    JournalEntryPO selectByVoucherNo(@Param("voucherNo") String voucherNo);

    @Select("SELECT * FROM t_journal_entry WHERE biz_no = #{bizNo} AND deleted = 0")
    JournalEntryPO selectByBizNo(@Param("bizNo") String bizNo);

    @Select("<script>" +
            "SELECT * FROM t_journal_entry WHERE deleted = 0" +
            "<if test='query.voucherNo != null and query.voucherNo != \"\"'> AND voucher_no = #{query.voucherNo}</if>" +
            "<if test='query.voucherDate != null'> AND voucher_date = #{query.voucherDate}</if>" +
            "<if test='query.fiscalPeriod != null and query.fiscalPeriod != \"\"'> AND fiscal_period = #{query.fiscalPeriod}</if>" +
            "<if test='query.bizType != null and query.bizType != \"\"'> AND biz_type = #{query.bizType}</if>" +
            "<if test='query.entryStatus != null and query.entryStatus != \"\"'> AND entry_status = #{query.entryStatus}</if>" +
            "<if test='query.accountCode != null and query.accountCode != \"\"'> AND account_code = #{query.accountCode}</if>" +
            "<if test='query.entryDirection != null and query.entryDirection != \"\"'> AND entry_direction = #{query.entryDirection}</if>" +
            "<if test='query.currency != null and query.currency != \"\"'> AND currency = #{query.currency}</if>" +
            "<if test='query.startDate != null'> AND voucher_date &gt;= #{query.startDate}</if>" +
            "<if test='query.endDate != null'> AND voucher_date &lt;= #{query.endDate}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<JournalEntryPO> pageQuery(Page<?> page, @Param("query") JournalQuery query);
}
