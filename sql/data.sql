-- 用户表 - 存储系统用户的基本信息和认证数据
CREATE TABLE users (
   id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一标识符',
   username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名，用于登录和显示',
   password VARCHAR(255) NOT NULL COMMENT '加密后的用户密码',
   email VARCHAR(100) UNIQUE COMMENT '用户邮箱，用于登录和通知',
   display_name VARCHAR(100) COMMENT '用户显示名称，用于界面展示',
   avatar_url VARCHAR(255) COMMENT '用户头像URL',
   bio TEXT COMMENT '用户个人简介',
   status ENUM('active', 'inactive', 'banned', 'deleted') DEFAULT 'active' COMMENT '用户状态：活跃/不活跃/被封禁/已删除',
   email_verified BOOLEAN DEFAULT FALSE COMMENT '邮箱是否已验证',
   role ENUM('user', 'editor', 'admin') DEFAULT 'user' COMMENT '用户角色：普通用户/编辑/管理员',
   locale VARCHAR(10) DEFAULT 'en-US' COMMENT '用户语言区域设置',
   timezone VARCHAR(50) DEFAULT 'UTC' COMMENT '用户时区设置',
   createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间',
   updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户信息最后更新时间',
   last_login_at DATETIME COMMENT '用户最后登录时间',
   social_provider VARCHAR(20) COMMENT '第三方登录提供商名称',
   social_id VARCHAR(255) COMMENT '第三方登录提供商中的用户ID',
   metadata JSON COMMENT '用户额外元数据，存储自定义信息',
   INDEX idx_username (username),
   INDEX idx_email (email),
   INDEX idx_created_at (createTime)
);
-- 博客表 - 存储用户发布的博客文章及相关信息
create table if not exists blog
(
    id         bigint auto_increment primary key comment '博客文章唯一标识符',
    userId     bigint                             not null comment '发布文章的用户ID，关联users表',
    title      varchar(512)                       null comment '文章标题',
    coverImg   varchar(1024)                      null comment '文章封面图片URL',
    content    text                               not null comment '文章正文内容，支持富文本',
    thumbCount int      default 0                 not null comment '文章获得的点赞总数',
    createTime datetime default CURRENT_TIMESTAMP not null comment '文章创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '文章最后更新时间'
    );
create index idx_userId on blog (userId);

-- 点赞记录表
create table if not exists thumb
(
    id         bigint auto_increment primary key,
    userId     bigint                             not null,
    blogId     bigint                             not null,
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间'
);
create unique index idx_userId_blogId on thumb (userId, blogId);
