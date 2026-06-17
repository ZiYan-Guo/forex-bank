package com.forex.settlement.infrastructure.mapper;

import com.forex.settlement.infrastructure.persistence.DocumentaryCollectionPO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Documentary collection MyBatis mapper.
 * 跟单托收数据访问层。
 */
@Mapper
public interface DocumentaryCollectionMapper extends BaseMapper<DocumentaryCollectionPO> {

    @Select("SELECT * FROM t_documentary_collection WHERE collection_no = #{collectionNo}")
    DocumentaryCollectionPO selectByCollectionNo(@Param("collectionNo") String collectionNo);
}
