-- ============================================
-- 银行外汇系统 - 认证模块数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS forex_auth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE forex_auth;

-- ----------------------------
-- 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=正常 0=禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 1=已删除 0=未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 系统角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL COMMENT '主键ID',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '角色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 1=已删除 0=未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- ----------------------------
-- 系统权限表
-- ----------------------------
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT NOT NULL COMMENT '主键ID',
    perm_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    perm_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    perm_type VARCHAR(20) NOT NULL DEFAULT 'BUTTON' COMMENT '权限类型: MENU/BUTTON/API',
    parent_code VARCHAR(100) DEFAULT NULL COMMENT '父权限编码',
    path VARCHAR(200) DEFAULT NULL COMMENT '请求路径',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 1=已删除 0=未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- ----------------------------
-- 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 1=已删除 0=未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 角色权限关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id BIGINT NOT NULL COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号(乐观锁)',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 1=已删除 0=未删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_id, permission_id),
    KEY idx_role_id (role_id),
    KEY idx_perm_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ----------------------------
-- 初始化数据
-- ----------------------------
-- 管理员用户 (密码: admin123, BCrypt 12 rounds)
INSERT INTO sys_user (id, username, password, real_name, email, status) VALUES
(1, 'admin', '$2a$12$Mdo6G9FKlctlNkBIVl29teXccMBhrPHlohA/2BlqAKlSUXtUx6GWq', '系统管理员', 'admin@forex-bank.com', 1);

-- 系统角色
INSERT INTO sys_role (id, role_code, role_name, description) VALUES
(1, 'ROLE_ADMIN', '系统管理员', '拥有所有权限'),
(2, 'ROLE_TRADER', '外汇交易员', '负责汇率报价、交易确认、敞口管理'),
(3, 'ROLE_SETTLEMENT_OPERATOR', '国际结算操作员', '负责信用证开立/通知、托收处理、单证审核'),
(4, 'ROLE_COMPLIANCE', '合规审核岗', '负责客户尽调、风险筛查、可疑交易报告审核'),
(5, 'ROLE_ACCOUNTANT', '财务/会计岗', '负责账务核算、对账管理、报表报送'),
(6, 'ROLE_CUSTOMER_MANAGER', '客户经理', '负责客户开户、产品推介、业务查询'),
(7, 'ROLE_TELLER', '支行柜员', '负责柜面结售汇、汇款受理、业务查询');

-- 系统权限
INSERT INTO sys_permission (id, perm_code, perm_name, perm_type, parent_code, path, sort_order) VALUES
(1, 'dashboard', '工作台', 'MENU', NULL, '/dashboard', 1),
(2, 'customer', '客户管理', 'MENU', NULL, '/customer', 2),
(3, 'customer:view', '客户查看', 'BUTTON', 'customer', NULL, 1),
(4, 'customer:edit', '客户编辑', 'BUTTON', 'customer', NULL, 2),
(5, 'customer:risk', '风险等级管理', 'BUTTON', 'customer', NULL, 3),
(6, 'exchange', '结售汇业务', 'MENU', NULL, '/exchange', 3),
(7, 'exchange:spot', '即期结售汇', 'BUTTON', 'exchange', NULL, 1),
(8, 'exchange:forward', '远期结售汇', 'BUTTON', 'exchange', NULL, 2),
(9, 'exchange:pending', '挂单结汇', 'BUTTON', 'exchange', NULL, 3),
(10, 'trading', '外汇买卖', 'MENU', NULL, '/trading', 4),
(11, 'trading:spot', '即期外汇买卖', 'BUTTON', 'trading', NULL, 1),
(12, 'trading:forward', '远期/掉期交易', 'BUTTON', 'trading', NULL, 2),
(13, 'trading:option', '外汇期权交易', 'BUTTON', 'trading', NULL, 3),
(14, 'payment', '跨境支付', 'MENU', NULL, '/payment', 5),
(15, 'payment:outward', '汇出汇款', 'BUTTON', 'payment', NULL, 1),
(16, 'payment:inward', '汇入汇款', 'BUTTON', 'payment', NULL, 2),
(17, 'payment:cips', 'CIPS支付', 'BUTTON', 'payment', NULL, 3),
(18, 'settlement', '国际结算', 'MENU', NULL, '/settlement', 6),
(19, 'settlement:lc', '信用证管理', 'BUTTON', 'settlement', NULL, 1),
(20, 'settlement:collection', '跟单托收', 'BUTTON', 'settlement', NULL, 2),
(21, 'settlement:guarantee', '国际保函', 'BUTTON', 'settlement', NULL, 3),
(22, 'settlement:finance', '贸易融资', 'BUTTON', 'settlement', NULL, 4),
(23, 'risk', '风险监测', 'MENU', NULL, '/risk', 7),
(24, 'risk:monitor', '交易监测', 'BUTTON', 'risk', NULL, 1),
(25, 'risk:report', '风险报告', 'BUTTON', 'risk', NULL, 2),
(26, 'risk:blacklist', '黑名单检索', 'BUTTON', 'risk', NULL, 3),
(27, 'reporting', '监管报送', 'MENU', NULL, '/reporting', 8),
(28, 'reporting:bop', '国际收支申报', 'BUTTON', 'reporting', NULL, 1),
(29, 'reporting:settlement', '结售汇申报', 'BUTTON', 'reporting', NULL, 2),
(30, 'reporting:capital', '资本项目申报', 'BUTTON', 'reporting', NULL, 3),
(31, 'account', '账户管理', 'MENU', NULL, '/account', 9),
(32, 'account:view', '账户查看', 'BUTTON', 'account', NULL, 1),
(33, 'account:manage', '账户操作', 'BUTTON', 'account', NULL, 2),
(34, 'rate', '汇率管理', 'MENU', NULL, '/rate', 10),
(35, 'rate:query', '汇率查询', 'BUTTON', 'rate', NULL, 1),
(36, 'rate:publish', '牌价发布', 'BUTTON', 'rate', NULL, 2),
(37, 'system', '系统管理', 'MENU', NULL, '/system', 99),
(38, 'system:user', '用户管理', 'BUTTON', 'system', NULL, 1),
(39, 'system:role', '角色管理', 'BUTTON', 'system', NULL, 2),
(40, 'system:config', '参数配置', 'BUTTON', 'system', NULL, 3);

-- 管理员角色分配所有权限
INSERT INTO sys_user_role (id, user_id, role_id) VALUES (1, 1, 1);
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES
(1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5),
(6, 1, 6), (7, 1, 7), (8, 1, 8), (9, 1, 9), (10, 1, 10),
(11, 1, 11), (12, 1, 12), (13, 1, 13), (14, 1, 14), (15, 1, 15),
(16, 1, 16), (17, 1, 17), (18, 1, 18), (19, 1, 19), (20, 1, 20),
(21, 1, 21), (22, 1, 22), (23, 1, 23), (24, 1, 24), (25, 1, 25),
(26, 1, 26), (27, 1, 27), (28, 1, 28), (29, 1, 29), (30, 1, 30),
(31, 1, 31), (32, 1, 32), (33, 1, 33), (34, 1, 34), (35, 1, 35),
(36, 1, 36), (37, 1, 37), (38, 1, 38), (39, 1, 39), (40, 1, 40);

-- 交易员角色权限
INSERT INTO sys_user_role (id, user_id, role_id) VALUES (2, 1, 2);
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES
(41, 2, 1), (42, 2, 6), (43, 2, 7), (44, 2, 8), (45, 2, 9),
(46, 2, 10), (47, 2, 11), (48, 2, 12), (49, 2, 13), (50, 2, 34), (51, 2, 35), (52, 2, 36);
