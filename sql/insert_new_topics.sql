-- ========================================
-- 新增话题数据插入脚本
-- 注意：本项目不使用数据库外键约束，所有关联关系通过程序逻辑约束维护
-- ========================================

-- 插入新的话题（生活类、娱乐类话题）
-- creator_id = 1 对应管理员用户，需要确保该用户在users表中存在
INSERT INTO topics (name, description, cover_image, color, is_official, creator_id) VALUES
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

-- 查看插入结果
SELECT id, name, description, color, is_official, create_time 
FROM topics 
WHERE name IN ('穿搭', '美食', '彩妆', '影视', '职场', '情感', '家居', '游戏', '旅行', '健身')
ORDER BY id; 