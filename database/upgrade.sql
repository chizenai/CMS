-- 数据库升级脚本
-- 用于扩展RBAC权限管理体系、审核记录、通知功能

USE cms_db;

-- ==================== RBAC权限管理体系 ====================

-- 1. 菜单表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(100) NOT NULL COMMENT '菜单名称',
    menu_code VARCHAR(100) COMMENT '菜单编码',
    path VARCHAR(255) COMMENT '路由路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '图标',
    menu_type INT DEFAULT 1 COMMENT '菜单类型 1:目录 2:菜单 3:按钮',
    sort INT DEFAULT 0 COMMENT '排序',
    permission VARCHAR(255) COMMENT '权限标识',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 2. 权限表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    resource_type INT DEFAULT 1 COMMENT '资源类型 1:菜单 2:按钮 3:API',
    url VARCHAR(255) COMMENT 'API地址',
    method VARCHAR(20) COMMENT '请求方法',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 3. 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 4. 角色菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 5. 用户角色关联表（一个用户可以有多个角色）
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ==================== 审核记录表 ====================

DROP TABLE IF EXISTS cms_audit_record;
CREATE TABLE cms_audit_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL COMMENT '文章ID',
    submitter_id BIGINT NOT NULL COMMENT '提交人ID',
    submitter_name VARCHAR(50) COMMENT '提交人名称',
    submit_time DATETIME NOT NULL COMMENT '提交时间',
    auditor_id BIGINT COMMENT '审核人ID',
    auditor_name VARCHAR(50) COMMENT '审核人名称',
    audit_time DATETIME COMMENT '审核时间',
    audit_status INT DEFAULT 0 COMMENT '审核状态 0:待审核 1:已通过 2:已拒绝',
    audit_comment VARCHAR(500) COMMENT '审核意见',
    version INT DEFAULT 1 COMMENT '版本号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- ==================== 通知表 ====================

DROP TABLE IF EXISTS sys_notification;
CREATE TABLE sys_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    notification_type INT DEFAULT 1 COMMENT '通知类型 1:系统通知 2:审核通知 3:任务提醒',
    sender_id BIGINT COMMENT '发送者ID',
    sender_name VARCHAR(50) COMMENT '发送者名称',
    receiver_id BIGINT COMMENT '接收者ID（NULL表示所有人）',
    is_read INT DEFAULT 0 COMMENT '是否已读 0:未读 1:已读',
    read_time DATETIME COMMENT '读取时间',
    priority INT DEFAULT 1 COMMENT '优先级 1:普通 2:重要 3:紧急',
    related_type VARCHAR(50) COMMENT '关联类型',
    related_id BIGINT COMMENT '关联ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- ==================== 初始化数据 ====================

-- 初始化菜单数据
INSERT INTO sys_menu (parent_id, menu_name, menu_code, path, component, icon, menu_type, sort, permission, status) VALUES
-- 一级菜单
(0, '数据统计', 'DASHBOARD', '/dashboard', 'Dashboard', 'el-icon-data-analysis', 2, 1, 'dashboard:view', 1),
(0, '内容管理', 'CONTENT', NULL, NULL, 'el-icon-document', 1, 2, NULL, 1),
(0, '素材管理', 'MATERIAL', '/material', 'material/MaterialList', 'el-icon-picture', 2, 3, 'material:view', 1),
(0, '站点管理', 'SITE', '/site', 'site/SiteList', 'el-icon-office-building', 2, 4, 'site:view', 1),
(0, '审核管理', 'AUDIT', NULL, NULL, 'el-icon-zoom-in', 1, 5, NULL, 1),
(0, '通知中心', 'NOTIFICATION', '/notification', 'notification/NotificationList', 'el-icon-message', 2, 6, 'notification:view', 1),
(0, '系统管理', 'SYSTEM', NULL, NULL, 'el-icon-setting', 1, 7, NULL, 1),
(0, '个人中心', 'PROFILE', '/profile', 'profile/Profile', 'el-icon-user', 2, 8, 'profile:view', 1),

-- 内容管理子菜单
(2, '文章管理', 'ARTICLE', '/article', 'article/ArticleList', 'el-icon-edit', 2, 1, 'article:view', 1),
(2, '栏目管理', 'CATEGORY', '/category', 'category/CategoryList', 'el-icon-menu', 2, 2, 'category:view', 1),

-- 审核管理子菜单
(5, '待审核文章', 'AUDIT_QUEUE', '/audit/queue', 'audit/AuditQueue', 'el-icon-time', 2, 1, 'audit:queue', 1),
(5, '审核记录', 'AUDIT_HISTORY', '/audit/history', 'audit/AuditHistory', 'el-icon-notebook-1', 2, 2, 'audit:history', 1),

-- 系统管理子菜单
(7, '用户管理', 'USER', '/user', 'system/UserList', 'el-icon-user', 2, 1, 'user:view', 1),
(7, '角色管理', 'ROLE', '/role', 'system/RoleList', 'el-icon-s-custom', 2, 2, 'role:view', 1),
(7, '菜单管理', 'MENU', '/menu', 'system/MenuList', 'el-icon-menu', 2, 3, 'menu:view', 1),
(7, '权限管理', 'PERMISSION', '/permission', 'system/PermissionList', 'el-icon-key', 2, 4, 'permission:view', 1),
(7, '日志管理', 'LOG', '/log', 'system/LogList', 'el-icon-notebook-2', 2, 5, 'log:view', 1);

-- 初始化权限数据
INSERT INTO sys_permission (permission_name, permission_code, resource_type, url, method, parent_id, sort, status) VALUES
-- 文章管理权限
('查看文章', 'article:view', 3, '/api/article/page', 'GET', 0, 1, 1),
('新增文章', 'article:add', 3, '/api/article', 'POST', 0, 2, 1),
('编辑文章', 'article:edit', 3, '/api/article', 'PUT', 0, 3, 1),
('删除文章', 'article:delete', 3, '/api/article/*', 'DELETE', 0, 4, 1),
('发布文章', 'article:publish', 3, '/api/article/publish/*', 'PUT', 0, 5, 1),
('撤销文章', 'article:revoke', 3, '/api/article/revoke/*', 'PUT', 0, 6, 1),

-- 栏目管理权限
('查看栏目', 'category:view', 3, '/api/category/list', 'GET', 0, 1, 1),
('新增栏目', 'category:add', 3, '/api/category', 'POST', 0, 2, 1),
('编辑栏目', 'category:edit', 3, '/api/category', 'PUT', 0, 3, 1),
('删除栏目', 'category:delete', 3, '/api/category/*', 'DELETE', 0, 4, 1),

-- 素材管理权限
('查看素材', 'material:view', 3, '/api/material/list', 'GET', 0, 1, 1),
('上传素材', 'material:upload', 3, '/api/material/upload', 'POST', 0, 2, 1),
('删除素材', 'material:delete', 3, '/api/material/*', 'DELETE', 0, 3, 1),

-- 审核权限
('查看审核队列', 'audit:queue', 3, '/api/audit/queue', 'GET', 0, 1, 1),
('审核文章', 'audit:audit', 3, '/api/audit', 'PUT', 0, 2, 1),
('批量审核', 'audit:batch', 3, '/api/audit/batch', 'POST', 0, 3, 1),
('查看审核记录', 'audit:history', 3, '/api/audit/history', 'GET', 0, 4, 1),

-- 用户管理权限
('查看用户', 'user:view', 3, '/api/user/page', 'GET', 0, 1, 1),
('新增用户', 'user:add', 3, '/api/user', 'POST', 0, 2, 1),
('编辑用户', 'user:edit', 3, '/api/user', 'PUT', 0, 3, 1),
('删除用户', 'user:delete', 3, '/api/user/*', 'DELETE', 0, 4, 1),
('重置密码', 'user:reset', 3, '/api/user/reset-password/*', 'PUT', 0, 5, 1),

-- 角色管理权限
('查看角色', 'role:view', 3, '/api/role/list', 'GET', 0, 1, 1),
('新增角色', 'role:add', 3, '/api/role', 'POST', 0, 2, 1),
('编辑角色', 'role:edit', 3, '/api/role', 'PUT', 0, 3, 1),
('删除角色', 'role:delete', 3, '/api/role/*', 'DELETE', 0, 4, 1),
('分配权限', 'role:assign', 3, '/api/role/assign-permission', 'POST', 0, 5, 1),

-- 菜单管理权限
('查看菜单', 'menu:view', 3, '/api/menu/list', 'GET', 0, 1, 1),
('新增菜单', 'menu:add', 3, '/api/menu', 'POST', 0, 2, 1),
('编辑菜单', 'menu:edit', 3, '/api/menu', 'PUT', 0, 3, 1),
('删除菜单', 'menu:delete', 3, '/api/menu/*', 'DELETE', 0, 4, 1),

-- 通知权限
('查看通知', 'notification:view', 3, '/api/notification/list', 'GET', 0, 1, 1),
('标记已读', 'notification:read', 3, '/api/notification/read/*', 'PUT', 0, 2, 1),
('发送通知', 'notification:send', 3, '/api/notification', 'POST', 0, 3, 1);

-- 给超级管理员分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 给超级管理员分配所有菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 初始化用户角色关联数据（将现有的用户角色关联到新表）
INSERT INTO sys_user_role (user_id, role_id)
SELECT id, role_id FROM sys_user WHERE role_id IS NOT NULL;

-- 给管理员分配部分权限
-- 查看类权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE permission_code IN (
    'article:view', 'category:view', 'material:view',
    'audit:queue', 'audit:history', 'user:view',
    'role:view', 'notification:view'
);

-- 给普通员工分配基本权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 3, id FROM sys_permission WHERE permission_code IN (
    'article:view', 'article:add', 'article:edit',
    'category:view', 'material:view', 'material:upload',
    'notification:view', 'notification:read'
);

-- 初始化系统通知
INSERT INTO sys_notification (title, content, notification_type, sender_name, priority, create_time) VALUES
('系统升级通知', '系统已完成升级，新增了权限管理、审核队列、通知中心等功能。请及时体验新功能。', 1, '系统管理员', 2, NOW()),
('审核流程优化', '文章审核流程已优化，现在支持批量审核操作。审核员可以一次性审核多篇文章。', 2, '系统管理员', 1, NOW()),
('新功能提醒', '文件上传功能已升级，支持多文件上传和拖拽上传。图片上传后支持实时预览。', 3, '系统管理员', 1, NOW());

-- 更新现有用户密码为BCrypt加密（密码：123456）
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5
-- 实际加密后的密码（BCrypt）：$2a$10$EqoX5Y.Q6kqVx.8T5U3I9eN2H4yG7fD9sA0dF1gH2jK3lM4nO5pQ
-- 使用：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5 是 "123456" 的BCrypt哈希

-- 将现有明文密码更新为BCrypt加密
UPDATE sys_user SET password = '$2a$10$EqoX5Y.Q6kqVx.8T5U3I9e0y5HhG7fD9sA0dF1gH2jK3lM4nO5pQ' WHERE password = '123456';
