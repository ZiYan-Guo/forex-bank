-- ============================================
-- OCR识别模块 - 数据库初始化脚本
-- ============================================

DROP TABLE IF EXISTS t_ocr_task;
CREATE TABLE t_ocr_task (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_id VARCHAR(64) NOT NULL COMMENT '任务ID',
    doc_type VARCHAR(30) NOT NULL COMMENT '单据类型: INVOICE/CONTRACT/LC_DOC/ID_CARD/BILL_OF_LADING',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
    ocr_result TEXT DEFAULT NULL COMMENT 'OCR识别结果(JSON)',
    recognized_fields TEXT DEFAULT NULL COMMENT '识别字段(JSON)',
    status VARCHAR(20) NOT NULL DEFAULT 'UPLOADED' COMMENT '状态: UPLOADED/PROCESSING/COMPLETED/FAILED',
    error_msg VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
    upload_time DATETIME NOT NULL COMMENT '上传时间',
    complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_id (task_id),
    KEY idx_doc_type (doc_type),
    KEY idx_status (status),
    KEY idx_upload_time (upload_time DESC),
    KEY idx_create_time (create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OCR识别任务表';
