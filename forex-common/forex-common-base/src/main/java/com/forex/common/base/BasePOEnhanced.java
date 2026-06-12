package com.forex.common.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 增强型基础 PO（持久化对象）
 * 支持乐观锁、逻辑删除、审计字段
 * 
 * 注意：此版本为改进版本，建议逐步替换现有 BasePO
 */
@Data
public class BasePOEnhanced {
    
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    protected Long id;
    
    /**
     * 版本号（用于乐观锁）
     */
    @Version
    protected Long version;
    
    /**
     * 逻辑删除标记（0 未删除，1 已删除）
     */
    @TableLogic(value = "0", delval = "1")
    protected Integer deleted;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createdAt;
    
    /**
     * 创建人 ID
     */
    protected Long createdBy;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updatedAt;
    
    /**
     * 更新人 ID
     */
    protected Long updatedBy;
    
    /**
     * 删除时间
     */
    protected LocalDateTime deletedAt;
    
    /**
     * 删除人 ID
     */
    protected Long deletedBy;
    
    /**
     * 备注
     */
    protected String remark;
}
