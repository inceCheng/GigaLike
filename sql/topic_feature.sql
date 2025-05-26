-- ========================================
-- 话题功能数据库设计
-- ========================================

-- 1. 话题表 - 存储所有话题信息
CREATE TABLE topics (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '话题唯一标识符',
    name VARCHAR(100) NOT NULL COMMENT '话题名称，如"前端开发"、"Java学习"',
    description TEXT COMMENT '话题描述',
    cover_image VARCHAR(255) COMMENT '话题封面图片URL',
    color VARCHAR(7) DEFAULT '#1890ff' COMMENT '话题主题色，十六进制颜色值',
    status ENUM('active', 'inactive', 'banned') DEFAULT 'active' COMMENT '话题状态：活跃/不活跃/被禁用',
    post_count INT UNSIGNED DEFAULT 0 COMMENT '使用该话题的帖子数量',
    follow_count INT UNSIGNED DEFAULT 0 COMMENT '关注该话题的用户数量',
    creator_id BIGINT UNSIGNED COMMENT '话题创建者ID，关联users表',
    is_official BOOLEAN DEFAULT FALSE COMMENT '是否为官方话题',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '话题创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '话题更新时间',
    
    UNIQUE KEY uk_name (name),
    INDEX idx_status (status),
    INDEX idx_post_count (post_count),
    INDEX idx_creator_id (creator_id),
    INDEX idx_create_time (create_time)
) COMMENT '话题表';

-- 2. 博客话题关联表 - 多对多关系
CREATE TABLE blog_topics (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '关联记录唯一标识符',
    blog_id BIGINT UNSIGNED NOT NULL COMMENT '博客ID，关联blog表',
    topic_id BIGINT UNSIGNED NOT NULL COMMENT '话题ID，关联topics表',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关联创建时间',
    
    UNIQUE KEY uk_blog_topic (blog_id, topic_id),
    INDEX idx_blog_id (blog_id),
    INDEX idx_topic_id (topic_id),
    INDEX idx_create_time (create_time)
) COMMENT '博客话题关联表';

-- 3. 用户话题关注表 - 用户可以关注感兴趣的话题
CREATE TABLE user_topic_follows (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '关注记录唯一标识符',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID，关联users表',
    topic_id BIGINT UNSIGNED NOT NULL COMMENT '话题ID，关联topics表',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    
    UNIQUE KEY uk_user_topic (user_id, topic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_topic_id (topic_id),
    INDEX idx_create_time (create_time)
) COMMENT '用户话题关注表';

-- 4. 话题热度统计表 - 用于记录话题的热度变化（可选，用于数据分析）
CREATE TABLE topic_statistics (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '统计记录唯一标识符',
    topic_id BIGINT UNSIGNED NOT NULL COMMENT '话题ID，关联topics表',
    date DATE NOT NULL COMMENT '统计日期',
    daily_posts INT UNSIGNED DEFAULT 0 COMMENT '当日新增帖子数',
    daily_views INT UNSIGNED DEFAULT 0 COMMENT '当日浏览量',
    daily_follows INT UNSIGNED DEFAULT 0 COMMENT '当日新增关注数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    
    UNIQUE KEY uk_topic_date (topic_id, date),
    INDEX idx_topic_id (topic_id),
    INDEX idx_date (date)
) COMMENT '话题热度统计表';

-- ========================================
-- 触发器 - 自动维护计数字段
-- ========================================

-- 当博客话题关联创建时，增加话题的帖子数量
DELIMITER $$
CREATE TRIGGER tr_blog_topics_insert 
AFTER INSERT ON blog_topics
FOR EACH ROW
BEGIN
    UPDATE topics SET post_count = post_count + 1 WHERE id = NEW.topic_id;
END$$

-- 当博客话题关联删除时，减少话题的帖子数量
CREATE TRIGGER tr_blog_topics_delete 
AFTER DELETE ON blog_topics
FOR EACH ROW
BEGIN
    UPDATE topics SET post_count = post_count - 1 WHERE id = OLD.topic_id;
END$$

-- 当用户关注话题时，增加话题的关注数量
CREATE TRIGGER tr_user_topic_follows_insert 
AFTER INSERT ON user_topic_follows
FOR EACH ROW
BEGIN
    UPDATE topics SET follow_count = follow_count + 1 WHERE id = NEW.topic_id;
END$$

-- 当用户取消关注话题时，减少话题的关注数量
CREATE TRIGGER tr_user_topic_follows_delete 
AFTER DELETE ON user_topic_follows
FOR EACH ROW
BEGIN
    UPDATE topics SET follow_count = follow_count - 1 WHERE id = OLD.topic_id;
END$$
DELIMITER ;

-- ========================================
-- 初始化数据
-- ========================================

-- 插入一些官方话题
INSERT INTO topics (name, description, cover_image, color, is_official, creator_id) VALUES
('前端开发', '前端技术讨论，包括HTML、CSS、JavaScript、React、Vue等', 'https://cdn.example.com/topics/frontend.jpg', '#61dafb', TRUE, 1),
('后端开发', '后端技术分享，包括Java、Python、Node.js、数据库等', 'https://cdn.example.com/topics/backend.jpg', '#68217a', TRUE, 1),
('移动开发', '移动应用开发技术，包括Android、iOS、Flutter、React Native等', 'https://cdn.example.com/topics/mobile.jpg', '#a4c639', TRUE, 1),
('人工智能', 'AI技术讨论，机器学习、深度学习、自然语言处理等', 'https://cdn.example.com/topics/ai.jpg', '#ff6b6b', TRUE, 1),
('开源项目', '开源项目分享与讨论', 'https://cdn.example.com/topics/opensource.jpg', '#28a745', TRUE, 1),
('职场经验', '程序员职场心得与经验分享', 'https://cdn.example.com/topics/career.jpg', '#ffc107', TRUE, 1),
('学习笔记', '技术学习笔记与心得体会', 'https://cdn.example.com/topics/learning.jpg', '#17a2b8', TRUE, 1),
('工具推荐', '开发工具、软件、插件推荐', 'https://cdn.example.com/topics/tools.jpg', '#6f42c1', TRUE, 1),
('算法数据结构', '算法学习与数据结构讨论', 'https://cdn.example.com/topics/algorithm.jpg', '#e83e8c', TRUE, 1),
('系统设计', '系统架构设计与最佳实践', 'https://cdn.example.com/topics/system-design.jpg', '#fd7e14', TRUE, 1),
('穿搭', '时尚穿搭分享，搭配技巧与风格展示', 'https://cdn.example.com/topics/fashion.jpg', '#ff69b4', TRUE, 1),
('美食', '美食制作、餐厅推荐、烹饪技巧分享', 'https://cdn.example.com/topics/food.jpg', '#ff8c00', TRUE, 1),
('彩妆', '化妆技巧、产品测评、美妆教程', 'https://cdn.example.com/topics/makeup.jpg', '#dc143c', TRUE, 1),
('影视', '电影电视剧推荐、影评、娱乐资讯', 'https://cdn.example.com/topics/movies.jpg', '#4b0082', TRUE, 1),
('职场', '职场技能、求职经验、职业规划', 'https://cdn.example.com/topics/workplace.jpg', '#2e8b57', TRUE, 1),
('情感', '情感交流、恋爱心得、人际关系', 'https://cdn.example.com/topics/emotion.jpg', '#ff1493', TRUE, 1),
('家居', '家居装修、生活用品、居家技巧', 'https://cdn.example.com/topics/home.jpg', '#8fbc8f', TRUE, 1),
('游戏', '游戏攻略、电竞资讯、游戏评测', 'https://cdn.example.com/topics/gaming.jpg', '#00ced1', TRUE, 1),
('旅行', '旅游攻略、景点推荐、旅行见闻', 'https://cdn.example.com/topics/travel.jpg', '#32cd32', TRUE, 1),
('健身', '健身教程、运动心得、健康生活', 'https://cdn.example.com/topics/fitness.jpg', '#ff4500', TRUE, 1);

-- 为现有博客添加话题标签（假设博客ID从1-11）
INSERT INTO blog_topics (blog_id, topic_id) VALUES
-- 博客1: "系统更新公告" -> 系统设计
(1, 10),
-- 博客2: "社区管理规范" -> 职场经验
(2, 6),
-- 博客3: "全栈开发入门指南" -> 前端开发, 后端开发, 学习笔记
(3, 1), (3, 2), (3, 7),
-- 博客4: "React vs Vue：2025年该选择哪个框架？" -> 前端开发
(4, 1),
-- 博客5: "我的开发工具箱：2025版" -> 工具推荐
(5, 8),
-- 博客6: "开源项目贡献指南" -> 开源项目, 职场经验
(6, 5), (6, 6),
-- 博客7: "GitHub Actions实战教程" -> 开源项目, 工具推荐
(7, 5), (7, 8),
-- 博客8: "我的第一个博客" -> 学习笔记
(8, 7),
-- 博客9: "学习SQL的心得" -> 后端开发, 学习笔记
(9, 2), (9, 7),
-- 博客10: "API测试的重要性" -> 后端开发, 工具推荐
(10, 2), (10, 8),
-- 博客11: "我最喜欢的编程语言" -> 学习笔记, 职场经验
(11, 7), (11, 6);

-- 添加一些用户关注话题的数据
INSERT INTO user_topic_follows (user_id, topic_id) VALUES
-- 用户2(johndoe)关注前端开发、后端开发、开源项目
(2, 1), (2, 2), (2, 5),
-- 用户3(github_12345)关注开源项目、工具推荐、系统设计
(3, 5), (3, 8), (3, 10),
-- 用户6(testuser1)关注学习笔记、前端开发
(6, 7), (6, 1),
-- 用户7(testuser2)关注后端开发、工具推荐
(7, 2), (7, 8);

-- ========================================
-- 常用查询示例
-- ========================================

-- 1. 查询某个博客的所有话题
-- SELECT t.* FROM topics t 
-- JOIN blog_topics bt ON t.id = bt.topic_id 
-- WHERE bt.blog_id = 3;

-- 2. 查询某个话题下的所有博客
-- SELECT b.* FROM blog b 
-- JOIN blog_topics bt ON b.id = bt.blog_id 
-- WHERE bt.topic_id = 1 
-- ORDER BY b.createTime DESC;

-- 3. 查询最热门的话题（按帖子数量排序）
-- SELECT * FROM topics 
-- WHERE status = 'active' 
-- ORDER BY post_count DESC 
-- LIMIT 10;

-- 4. 查询用户关注的话题
-- SELECT t.* FROM topics t 
-- JOIN user_topic_follows utf ON t.id = utf.topic_id 
-- WHERE utf.user_id = 2;

-- 5. 搜索话题（模糊匹配）
-- SELECT * FROM topics 
-- WHERE name LIKE '%开发%' OR description LIKE '%开发%' 
-- AND status = 'active' 
-- ORDER BY post_count DESC; 