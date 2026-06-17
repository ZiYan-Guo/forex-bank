package com.forex.auth.infrastructure.mapper;

import com.forex.auth.infrastructure.persistence.PermissionPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Permission MyBatis mapper.
 * 权限数据访问层。
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionPO> {
}
