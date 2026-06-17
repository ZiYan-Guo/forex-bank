package com.forex.auth.infrastructure.mapper;

import com.forex.auth.infrastructure.persistence.RolePO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Role MyBatis mapper.
 * 角色数据访问层。
 */
@Mapper
public interface RoleMapper extends BaseMapper<RolePO> {

    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode}")
    RolePO selectByRoleCode(@Param("roleCode") String roleCode);
}
