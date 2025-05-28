-- 消息通知系统数据库表设计

-- 1. 通知表 (notifications)
CREATE TABLE `notifications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知唯一标识符',
    `user_id` BIGINT NOT NULL COMMENT '接收通知的用户ID',
    `sender_id` BIGINT NULL COMMENT '发送通知的用户ID（系统通知时为NULL）',
    `type` VARCHAR(50) NOT NULL COMMENT '通知类型：LIKE, COMMENT, FOLLOW, SYSTEM',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `related_id` BIGINT NULL COMMENT '关联的资源ID（如博客ID、评论ID等）',
    `related_type` VARCHAR(50) NULL COMMENT '关联资源类型：BLOG, COMMENT, USER',
    `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `read_time` DATETIME NULL COMMENT '阅读时间',
    `extra_data` JSON NULL COMMENT '额外数据（如博客标题、用户头像等）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id_create_time` (`user_id`, `create_time` DESC),
    INDEX `idx_user_id_is_read` (`user_id`, `is_read`),
    INDEX `idx_type_create_time` (`type`, `create_time` DESC),
    INDEX `idx_related_id_type` (`related_id`, `related_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 2. 通知设置表 (notification_settings)
CREATE TABLE `notification_settings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设置唯一标识符',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `like_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '点赞通知开关',
    `comment_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '评论通知开关',
    `follow_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '关注通知开关',
    `system_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '系统通知开关',
    `email_enabled` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '邮件通知开关',
    `push_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '推送通知开关',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知设置表';

-- 3. 通知模板表 (notification_templates)
CREATE TABLE `notification_templates` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板唯一标识符',
    `type` VARCHAR(50) NOT NULL COMMENT '通知类型',
    `title_template` VARCHAR(200) NOT NULL COMMENT '标题模板',
    `content_template` TEXT NOT NULL COMMENT '内容模板',
    `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';

-- 4. 通知统计表 (notification_stats)
CREATE TABLE `notification_stats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计唯一标识符',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_count` INT NOT NULL DEFAULT 0 COMMENT '总通知数',
    `unread_count` INT NOT NULL DEFAULT 0 COMMENT '未读通知数',
    `last_read_time` DATETIME NULL COMMENT '最后阅读时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知统计表';

-- 插入默认通知模板
INSERT INTO `notification_templates` (`type`, `title_template`, `content_template`) VALUES
('LIKE', '收到新的点赞', '{senderName} 点赞了你的文章《{blogTitle}》'),
('COMMENT', '收到新的评论', '{senderName} 评论了你的文章《{blogTitle}》'),
('FOLLOW', '收到新的关注', '{senderName} 关注了你'),
('SYSTEM', '系统通知', '{content}');

-- 创建索引优化查询性能
-- 复合索引：用户ID + 创建时间（用于分页查询）
CREATE INDEX `idx_notifications_user_time` ON `notifications` (`user_id`, `create_time` DESC);

-- 复合索引：用户ID + 是否已读 + 创建时间（用于未读消息查询）
CREATE INDEX `idx_notifications_user_read_time` ON `notifications` (`user_id`, `is_read`, `create_time` DESC);

-- 复合索引：关联ID + 关联类型（用于查询特定资源的通知）
CREATE INDEX `idx_notifications_related` ON `notifications` (`related_id`, `related_type`); 