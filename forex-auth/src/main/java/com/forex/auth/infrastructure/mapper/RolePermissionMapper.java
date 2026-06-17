package com.forex.auth.infrastructure.mapper;

import com.forex.auth.infrastructure.persistence.RolePermissionPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Role-permission MyBatis mapper.
 * 角色权限关联数据访问层。
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionPO> {

    @Select("SELECT * FROM sys_role_permission WHERE role_code = #{roleCode}")
    List<RolePermissionPO> selectByRoleCode(@Param("roleCode") String roleCode);
}
