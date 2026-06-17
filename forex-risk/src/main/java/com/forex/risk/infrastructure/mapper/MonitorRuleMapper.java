package com.forex.risk.infrastructure.mapper;

import com.forex.risk.infrastructure.persistence.MonitorRulePO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MonitorRuleMapper extends BaseMapper<MonitorRulePO> {

    @Select("SELECT * FROM t_monitor_rule WHERE rule_code = #{ruleCode}")
    MonitorRulePO selectByRuleCode(@Param("ruleCode") String ruleCode);

    @Select("SELECT * FROM t_monitor_rule WHERE is_enabled = 1")
    List<MonitorRulePO> selectAllEnabled();
}
