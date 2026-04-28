-- 创建数据库
CREATE DATABASE IF NOT EXISTS cms_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE cms_db;

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role_id BIGINT COMMENT '角色ID',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 栏目表
DROP TABLE IF EXISTS cms_category;
CREATE TABLE cms_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '栏目名称',
    code VARCHAR(50) COMMENT '栏目编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    sort INT DEFAULT 0 COMMENT '排序',
    level INT DEFAULT 1 COMMENT '层级',
    path VARCHAR(255) COMMENT '路径',
    type INT DEFAULT 1 COMMENT '类型 1:文章栏目 2:图文栏目',
    icon VARCHAR(255) COMMENT '图标',
    description VARCHAR(255) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='栏目表';

-- 文章表
DROP TABLE IF EXISTS cms_article;
CREATE TABLE cms_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    summary VARCHAR(500) COMMENT '摘要',
    cover VARCHAR(255) COMMENT '封面图',
    content LONGTEXT COMMENT '内容',
    category_id BIGINT COMMENT '栏目ID',
    type INT DEFAULT 1 COMMENT '类型 1:文章 2:图文',
    author_id BIGINT COMMENT '作者ID',
    status INT DEFAULT 0 COMMENT '状态 0:草稿 1:待审核 2:已发布 3:已拒绝',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    publish_time DATETIME COMMENT '发布时间',
    auditor_id BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    audit_comment VARCHAR(500) COMMENT '审核意见'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 素材表
DROP TABLE IF EXISTS cms_material;
CREATE TABLE cms_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT '素材名称',
    type VARCHAR(50) NOT NULL COMMENT '类型: image, video, audio, file',
    url VARCHAR(500) NOT NULL COMMENT '访问地址',
    thumbnail VARCHAR(500) COMMENT '缩略图',
    size BIGINT COMMENT '文件大小(字节)',
    description VARCHAR(500) COMMENT '描述',
    category_id BIGINT COMMENT '分类ID',
    uploader_id BIGINT COMMENT '上传者ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='素材表';

-- 站点表
DROP TABLE IF EXISTS cms_site;
CREATE TABLE cms_site (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '站点名称',
    domain VARCHAR(255) COMMENT '域名',
    logo VARCHAR(255) COMMENT 'Logo',
    description VARCHAR(500) COMMENT '描述',
    keywords VARCHAR(500) COMMENT '关键词',
    layout TEXT COMMENT '布局配置(JSON)',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点表';

-- 访问记录表
DROP TABLE IF EXISTS cms_visit;
CREATE TABLE cms_visit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    page_url VARCHAR(500) COMMENT '访问页面',
    article_id BIGINT COMMENT '文章ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问记录表';

-- 日志表
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module VARCHAR(50) COMMENT '模块',
    operation VARCHAR(50) COMMENT '操作',
    method VARCHAR(200) COMMENT '方法',
    params TEXT COMMENT '参数',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    ip VARCHAR(50) COMMENT 'IP地址',
    status INT DEFAULT 1 COMMENT '状态 1:成功 0:失败',
    error_msg TEXT COMMENT '错误信息',
    time BIGINT COMMENT '耗时(毫秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 插入初始角色数据
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('Super Admin', 'SUPER_ADMIN', 'Has all system permissions', 1),
('Admin', 'ADMIN', 'Has partial admin permissions', 1),
('Employee', 'EMPLOYEE', 'Regular employee permissions', 1);

-- 插入初始管理员账号 (密码: 123456)
INSERT INTO sys_user (username, password, nickname, email, phone, role_id, status) VALUES
('admin', '123456', 'Super Admin', 'admin@cms.com', '13800138000', 1, 1),
('manager', '123456', 'Admin', 'manager@cms.com', '13800138001', 2, 1);

-- 插入初始栏目数据
INSERT INTO cms_category (name, code, parent_id, sort, level, type, description, status) VALUES
('Home', 'HOME', 0, 0, 1, 1, 'Website Home', 1),
('News', 'NEWS', 0, 1, 1, 1, 'News Section', 1),
('Products', 'PRODUCT', 0, 2, 1, 2, 'Product Display', 1),
('About', 'ABOUT', 0, 3, 1, 1, 'About Us', 1),
('Contact', 'CONTACT', 0, 4, 1, 1, 'Contact Us', 1),
('Company News', 'COMPANY_NEWS', 2, 1, 2, 1, 'Company Internal News', 1),
('Industry News', 'INDUSTRY_NEWS', 2, 2, 2, 1, 'Industry Latest News', 1),
('Product Category 1', 'PRODUCT_1', 3, 1, 2, 2, 'Product Category 1', 1),
('Product Category 2', 'PRODUCT_2', 3, 2, 2, 2, 'Product Category 2', 1);

-- 插入初始站点数据
INSERT INTO cms_site (name, domain, description, keywords, status) VALUES
('Enterprise CMS System', 'http://localhost:8080', 'Enterprise CMS System based on Vue + SpringBoot', 'CMS,Enterprise Website,Content Management System', 1);
