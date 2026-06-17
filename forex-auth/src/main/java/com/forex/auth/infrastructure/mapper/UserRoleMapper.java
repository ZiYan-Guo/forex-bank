package com.forex.auth.infrastructure.mapper;

import com.forex.auth.infrastructure.persistence.UserRolePO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * User-role MyBatis mapper.
 * 用户角色关联数据访问层。
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePO> {

    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId}")
    List<UserRolePO> selectByUserId(@Param("userId") Long userId);
}
