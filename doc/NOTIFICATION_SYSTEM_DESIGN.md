# GigaLike 消息通知系统设计文档

## 1. 系统概述

### 1.1 功能描述
GigaLike消息通知系统为用户提供实时的互动通知功能，当用户的帖子被点赞、评论或被其他用户关注时，系统会自动发送通知给相关用户。

### 1.2 技术架构
- **后端框架**: Spring Boot 3.2.3
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **消息队列**: Apache Pulsar
- **ORM框架**: MyBatis-Plus
- **前端**: Vue 3

### 1.3 设计原则
- **异步处理**: 使用Pulsar消息队列实现异步通知处理
- **高性能**: 通过Redis缓存和数据库索引优化查询性能
- **可扩展**: 支持多种通知类型，易于扩展新的通知场景
- **用户体验**: 提供通知开关，用户可自定义通知偏好

## 2. 数据库设计

### 2.1 核心表结构

#### 2.1.1 通知表 (notifications)
```sql
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
```

#### 2.1.2 通知设置表 (notification_settings)
```sql
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
```

### 2.2 索引设计
- **主查询索引**: `idx_user_id_create_time` - 支持用户通知列表分页查询
- **未读查询索引**: `idx_user_id_is_read` - 支持未读通知快速查询
- **类型查询索引**: `idx_type_create_time` - 支持按类型查询通知
- **关联查询索引**: `idx_related_id_type` - 支持查询特定资源的通知

## 3. 系统架构

### 3.1 整体架构图
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端 Vue3     │    │  Spring Boot    │    │   MySQL数据库   │
│                 │◄──►│   后端服务      │◄──►│                 │
│ - 通知列表      │    │                 │    │ - 通知数据      │
│ - 未读数量      │    │ - REST API      │    │ - 用户设置      │
│ - 实时推送      │    │ - 业务逻辑      │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │  Apache Pulsar  │    │   Redis缓存     │
                       │                 │    │                 │
                       │ - 通知事件队列  │    │ - 会话缓存      │
                       │ - 异步处理      │    │ - 点赞缓存      │
                       │ - 消息重试      │    │                 │
                       └─────────────────┘    └─────────────────┘
```

### 3.2 消息流程
1. **事件触发**: 用户点赞帖子
2. **事件发布**: 点赞服务发布通知事件到Pulsar
3. **事件消费**: 通知消费者接收并处理事件
4. **通知创建**: 根据事件信息创建通知记录
5. **实时推送**: 通过WebSocket推送给前端（可选）

## 4. 核心功能

### 4.1 通知类型
- **LIKE**: 点赞通知 - 当用户的帖子被点赞时发送
- **COMMENT**: 评论通知 - 当用户的帖子被评论时发送
- **FOLLOW**: 关注通知 - 当用户被其他人关注时发送
- **SYSTEM**: 系统通知 - 系统管理员发送的通知

### 4.2 API接口

#### 4.2.1 查询通知列表
```http
POST /api/notification/list
Content-Type: application/json

{
    "current": 1,
    "pageSize": 10,
    "isRead": 0,
    "type": "LIKE"
}
```

#### 4.2.2 获取未读数量
```http
GET /api/notification/unread/count
```

#### 4.2.3 标记为已读
```http
POST /api/notification/read/{id}
```

#### 4.2.4 批量标记已读
```http
POST /api/notification/read/all
```

#### 4.2.5 删除通知
```http
DELETE /api/notification/{id}
```

### 4.3 响应格式
```json
{
    "code": 0,
    "message": "ok",
    "data": {
        "records": [
            {
                "id": 1,
                "type": "LIKE",
                "title": "收到新的点赞",
                "content": "张三 点赞了你的文章《Spring Boot实战》",
                "relatedId": 123,
                "relatedType": "BLOG",
                "isRead": 0,
                "readTime": null,
                "createTime": "2024-01-15T10:30:00",
                "sender": {
                    "id": 456,
                    "username": "zhangsan",
                    "displayName": "张三",
                    "avatarUrl": "https://example.com/avatar.jpg"
                },
                "extraData": {
                    "blogTitle": "Spring Boot实战",
                    "blogId": 123
                }
            }
        ],
        "total": 50,
        "current": 1,
        "size": 10
    }
}
```

## 5. 技术实现

### 5.1 核心类设计

#### 5.1.1 实体类
- `Notification`: 通知实体
- `NotificationSettings`: 通知设置实体

#### 5.1.2 枚举类
- `NotificationTypeEnum`: 通知类型枚举
- `RelatedTypeEnum`: 关联资源类型枚举

#### 5.1.3 事件类
- `NotificationEvent`: 通知事件，用于Pulsar消息传递

#### 5.1.4 服务类
- `NotificationService`: 通知业务逻辑
- `NotificationConsumer`: Pulsar消息消费者

### 5.2 点赞通知集成
在现有的`ThumbServiceMQImpl`中集成通知功能：

```java
// 发送点赞通知
private void sendLikeNotification(Long likerId, Long blogId) {
    // 获取博客信息
    Blog blog = blogService.getById(blogId);
    Long authorId = blog.getUserid();
    
    // 不给自己发通知
    if (likerId.equals(authorId)) {
        return;
    }
    
    // 创建通知事件
    NotificationEvent event = NotificationEvent.builder()
            .userId(authorId)
            .senderId(likerId)
            .type(NotificationTypeEnum.LIKE.getCode())
            .relatedId(blogId)
            .relatedType(RelatedTypeEnum.BLOG.getCode())
            .extraData(Map.of("blogTitle", blog.getTitle()))
            .eventTime(LocalDateTime.now())
            .build();
    
    // 发送到Pulsar
    notificationPulsarTemplate.sendAsync("notification-topic", event);
}
```

### 5.3 消息队列配置
使用现有的Pulsar配置，新增通知主题：
- **主题名称**: `notification-topic`
- **订阅名称**: `notification-subscription`
- **订阅类型**: `Shared`（支持多实例消费）

## 6. 性能优化

### 6.1 数据库优化
- **索引优化**: 为常用查询字段创建复合索引
- **分页查询**: 使用MyBatis-Plus的分页插件
- **批量操作**: 支持批量标记已读操作

### 6.2 缓存策略
- **Redis缓存**: 缓存用户未读通知数量
- **本地缓存**: 缓存通知模板和用户设置

### 6.3 异步处理
- **消息队列**: 使用Pulsar实现异步通知处理
- **批量处理**: 支持批量消费通知事件

## 7. 扩展功能

### 7.1 实时推送
- **WebSocket**: 实现实时通知推送
- **Server-Sent Events**: 轻量级实时推送方案

### 7.2 邮件通知
- **邮件模板**: 支持HTML邮件模板
- **异步发送**: 通过消息队列异步发送邮件

### 7.3 推送通知
- **移动端推送**: 集成第三方推送服务
- **浏览器推送**: 支持Web Push API

## 8. 监控与运维

### 8.1 监控指标
- **通知发送量**: 每日/每小时通知发送统计
- **消息队列积压**: Pulsar消息积压监控
- **处理延迟**: 通知处理时间监控

### 8.2 日志记录
- **业务日志**: 记录通知创建、发送、读取等操作
- **错误日志**: 记录异常情况和失败重试

### 8.3 数据清理
- **定时清理**: 定期清理用户的旧通知（保留最近1000条）
- **归档策略**: 长期通知数据归档方案

## 9. 安全考虑

### 9.1 权限控制
- **用户隔离**: 用户只能查看自己的通知
- **操作权限**: 只能操作属于自己的通知

### 9.2 数据验证
- **参数校验**: 严格校验API请求参数
- **SQL注入防护**: 使用参数化查询

### 9.3 频率限制
- **API限流**: 防止恶意请求
- **通知去重**: 避免重复通知

## 10. 部署说明

### 10.1 数据库初始化
1. 执行`sql/notification_system.sql`创建表结构
2. 插入默认通知模板数据

### 10.2 配置要求
- **Pulsar**: 确保Pulsar服务正常运行
- **Redis**: 配置Redis连接信息
- **MySQL**: 配置数据库连接

### 10.3 启动顺序
1. 启动Pulsar服务
2. 启动Redis服务
3. 启动MySQL服务
4. 启动Spring Boot应用

## 11. 测试方案

### 11.1 单元测试
- **Service层测试**: 测试通知业务逻辑
- **Mapper层测试**: 测试数据库操作

### 11.2 集成测试
- **API测试**: 测试通知相关接口
- **消息队列测试**: 测试Pulsar消息处理

### 11.3 性能测试
- **并发测试**: 测试高并发场景下的通知处理
- **压力测试**: 测试系统在大量通知下的表现

## 12. 总结

GigaLike消息通知系统采用现代化的微服务架构，通过消息队列实现异步处理，保证了系统的高性能和可扩展性。系统设计充分考虑了用户体验、性能优化和安全性，为用户提供了完善的通知功能。

通过本系统，用户可以及时收到点赞、评论、关注等互动通知，提升了平台的用户粘性和活跃度。 