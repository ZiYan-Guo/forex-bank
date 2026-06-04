package com.forex.ai.infrastructure.mapper;

import com.forex.common.mybatis.base.BaseMapperExt;
import com.forex.ai.infrastructure.persistence.DocumentAuditPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DocumentAuditMapper extends BaseMapperExt<DocumentAuditPO> {

    @Select("SELECT * FROM t_document_audit WHERE audit_id = #{auditId} AND deleted = 0")
    DocumentAuditPO findByAuditId(@Param("auditId") String auditId);

    @Select("SELECT * FROM t_document_audit WHERE biz_no = #{bizNo} AND deleted = 0")
    DocumentAuditPO findByBizNo(@Param("bizNo") String bizNo);
}
