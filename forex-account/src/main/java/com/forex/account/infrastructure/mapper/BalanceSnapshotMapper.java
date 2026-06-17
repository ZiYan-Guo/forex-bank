package com.forex.account.infrastructure.mapper;

import com.forex.account.infrastructure.persistence.BalanceSnapshotPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Balance snapshot MyBatis mapper.
 * 余额快照数据访问层。
 */
@Mapper
public interface BalanceSnapshotMapper extends BaseMapper<BalanceSnapshotPO> {
}
