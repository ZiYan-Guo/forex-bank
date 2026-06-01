package com.forex.auth.infrastructure.mapper;

import com.forex.auth.infrastructure.persistence.PermissionPO;
import com.forex.auth.infrastructure.persistence.RolePO;
import com.forex.auth.infrastructure.persistence.UserPO;

import com.forex.common.mybatis.base.BaseMapperExt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapperExt<UserPO> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    UserPO selectByUsername(@Param("username") String username);

    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND ur.deleted = 0 AND r.deleted = 0")
    List<RolePO> selectRolesByUserId(@Param("userId") Long userId);

    @Select("<script>" +
            "SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id IN " +
            "<foreach collection='roleIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "AND rp.deleted = 0 AND p.deleted = 0" +
            "</script>")
    List<PermissionPO> selectPermissionsByRoleIds(@Param("roleIds") List<Long> roleIds);

    @Select("SELECT * FROM sys_permission WHERE deleted = 0 ORDER BY sort_order")
    List<PermissionPO> selectAllPermissions();
}
