package com.forex.settlement.infrastructure.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.settlement.infrastructure.persistence.DocumentaryCollectionPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CollectionMapper extends BaseMapperExt<DocumentaryCollectionPO> {

    @Select("SELECT * FROM t_documentary_collection WHERE collection_no = #{collectionNo} AND deleted = 0")
    DocumentaryCollectionPO selectByCollectionNo(@Param("collectionNo") String collectionNo);

    @Select("<script>" +
            "SELECT * FROM t_documentary_collection WHERE deleted = 0" +
            "<if test='query.collectionNo != null and query.collectionNo != \"\"'> AND collection_no = #{query.collectionNo}</if>" +
            "<if test='query.customerId != null'> AND customer_id = #{query.customerId}</if>" +
            "<if test='query.collectionStatus != null and query.collectionStatus != \"\"'> AND collection_status = #{query.collectionStatus}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<DocumentaryCollectionPO> pageQuery(Page<DocumentaryCollectionPO> page,
                                             @Param("query") com.forex.settlement.domain.model.query.CollectionQuery query);
}
