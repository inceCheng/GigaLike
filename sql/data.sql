-- 用户表 - 存储系统用户的基本信息和认证数据
CREATE TABLE users (
   id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一标识符',
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

-- 管理员用户（系统内置）
INSERT INTO users (
    id, username, password, email, display_name, avatar_url, bio,
    status, email_verified, role, locale, timezone,
    createtime, updatetime, last_login_at,
    social_provider, social_id, metadata
) VALUES (
             1,
             'admin',
             '21232f297a57a5a743894a0e4a801fc3', -- MD5加密的"admin"
             'admin@example.com',
             '系统管理员',
             'https://cdn.example.com/avatars/admin.png',
             '系统管理员账户',
             'ACTIVE', -- 状态枚举示例
             1,
             'ADMIN',
             'zh-CN',
             'Asia/Shanghai',
             '2023-01-01 00:00:00',
             NOW(),
             NOW(),
             NULL,
             NULL,
             JSON_OBJECT('internal_id', 1000, 'security_level', 5)
         );

-- 普通用户（邮箱注册）
INSERT INTO users (
    username, password, email, display_name, avatar_url, bio,
    status, email_verified, role, locale, timezone,
    createtime, updatetime, last_login_at,
    social_provider, social_id, metadata
) VALUES (
             'johndoe',
             'e10adc3949ba59abbe56e057f20f883e', -- MD5加密的"123456"
             'john.doe@example.com',
             'John Doe',
             'https://cdn.example.com/avatars/johndoe.jpg',
             '全栈开发者 | 技术爱好者',
             'ACTIVE',
             1,
             'USER',
             'en-US',
             'UTC',
             NOW() - INTERVAL 7 DAY,
             NOW(),
             NOW() - INTERVAL 1 HOUR,
             NULL,
             NULL,
             JSON_OBJECT('theme', 'dark', 'notification_prefs', true)
         );

-- 第三方登录用户（GitHub）
INSERT INTO users (
    username, password, email, display_name, avatar_url, bio,
    status, email_verified, role, locale, timezone,
    createtime, updatetime, last_login_at,
    social_provider, social_id, metadata
) VALUES (
             'github_12345',
             'e10adc3949ba59abbe56e057f20f883e', -- MD5加密的"123456"
             'octocat@github.com',
             'GitHub User',
             'https://avatars.githubusercontent.com/u/12345',
             'Open source contributor',
             'ACTIVE',
             0, -- 第三方邮箱未验证
             'USER',
             'en-GB',
             'Europe/London',
             NOW() - INTERVAL 3 DAY,
             NOW(),
             NOW() - INTERVAL 30 MINUTE,
             'GITHUB',
             '123456789',
             JSON_OBJECT('public_repos', 15, 'followers', 42)
         );

-- 封禁用户（违规账户）
INSERT INTO users (
    username, password, email, display_name, avatar_url, bio,
    status, email_verified, role, locale, timezone,
    createtime, updatetime, last_login_at,
    social_provider, social_id, metadata
) VALUES (
             'spammer_2023',
             'e10adc3949ba59abbe56e057f20f883e', -- MD5加密的"123456"
             'spam@badactor.com',
             'Spammer',
             NULL,
             NULL,
             'BANNED',
             0,
             'USER',
             'ru-RU',
             'Europe/Moscow',
             NOW() - INTERVAL 2 DAY,
             NOW(),
             NOW() - INTERVAL 1 DAY,
             NULL,
             NULL,
             JSON_OBJECT('ban_reason', '发送垃圾信息', 'ban_duration', 'PERMANENT')
         );

-- 未激活用户（新注册）
INSERT INTO users (
    username, password, email, display_name, avatar_url, bio,
    status, email_verified, role, locale, timezone,
    createtime, updatetime, last_login_at,
    social_provider, social_id, metadata
) VALUES (
             'new_user_001',
             'e10adc3949ba59abbe56e057f20f883e', -- MD5加密的"123456"
             'new.user@example.com',
             '待激活用户',
             'https://cdn.example.com/avatars/default.png',
             NULL,
             'deleted',
             0,
             'user',
             'ja-JP',
             'Asia/Tokyo',
             NOW(),
             NOW(),
             NULL,
             NULL,
             NULL,
             JSON_OBJECT('activation_token', 'a1b2c3d4e5', 'token_expiry', NOW() + INTERVAL 1 DAY)
         );

-- 为博客表插入模拟数据
-- 管理员用户(ID: 1)的博客
INSERT INTO blog (userId, title, coverImg, content, thumbCount, createTime, updateTime) VALUES
    (1, '系统更新公告：GigaLike 2.0 发布', 'https://cdn.example.com/blogs/system-update.jpg',
     '<h1>GigaLike 2.0 重大更新</h1><p>我们很高兴地宣布，GigaLike 2.0 版本已正式发布！此次更新包含众多新功能和性能优化...</p><ul><li>全新的用户界面</li><li>性能提升50%</li><li>新增AI推荐功能</li></ul>',
     3, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 2 DAY);

INSERT INTO blog (userId, title, coverImg, content, thumbCount, createTime, updateTime) VALUES
    (1, '社区管理规范', 'https://cdn.example.com/blogs/community-rules.jpg',
     '<h2>GigaLike社区规范</h2><p>为了维护良好的社区环境，请所有用户遵循以下规则：</p><ol><li>尊重他人，禁止人身攻击</li><li>禁止发布违规内容</li><li>原创内容请标明出处</li></ol>',
     5, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 20 DAY);

-- 普通用户johndoe(假设ID: 2)的博客
INSERT INTO blog (userId, title, coverImg, content, thumbCount, createTime, updateTime) VALUES
    (2, '全栈开发入门指南', 'https://cdn.example.com/blogs/fullstack-guide.jpg',
     '<h1>如何成为一名全栈开发者</h1><p>在这篇文章中，我将分享我成为全栈开发者的经验和学习路径...</p><h2>前端技能</h2><p>HTML, CSS, JavaScript是必备的基础...</p><h2>后端技能</h2><p>选择一门后端语言，如Java, Python或Node.js...</p>',
     8, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY);

INSERT INTO blog (userId, title, coverImg, content, thumbCount, createTime, updateTime) VALUES
    (2, 'React vs Vue：2025年该选择哪个框架？', 'https://cdn.example.com/blogs/react-vs-vue.jpg',
     '<h1>React vs Vue：2025年前端框架对比</h1><p>前端框架的选择对项目至关重要，本文将对比React和Vue在2025年的表现...</p><h2>性能对比</h2><p>在渲染速度方面，Vue3的最新优化使其在大型列表渲染时略胜一筹...</p><h2>生态系统</h2><p>React仍然拥有最大的组件生态系统，但Vue正在迎头赶上...</p>',
     12, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 1 DAY);

INSERT INTO blog (userId, title, coverImg, content, thumbCount, createTime, updateTime) VALUES
    (2, '我的开发工具箱：2025版', 'https://cdn.example.com/blogs/dev-tools.jpg',
     '<h1>提升效率的开发工具推荐</h1><p>作为一名开发者，合适的工具能大大提高我们的工作效率。以下是我每天使用的开发工具...</p><h2>编辑器</h2><p>VS Code配合这些插件可以大大提升开发体验...</p><h2>终端工具</h2><p>推荐使用Oh My Zsh配合以下插件...</p>',
     7, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY);

-- GitHub用户(假设ID: 3)的博客
INSERT INTO blog (userId, title, coverImg, content, thumbCount, createTime, updateTime) VALUES
    (3, '开源项目贡献指南', 'https://cdn.example.com/blogs/opensource.jpg',
     '<h1>如何成为一名活跃的开源贡献者</h1><p>开源贡献不仅能帮助社区，还能提升个人技能和知名度...</p><h2>寻找合适的项目</h2><p>建议从这些标有"good first issue"的项目开始...</p><h2>提交高质量PR</h2><p>一个好的Pull Request应该包含这些要素...</p>',
     15, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY);

INSERT INTO blog (userId, title, coverImg, content, thumbCount, createTime, updateTime) VALUES
    (3, 'GitHub Actions实战教程', 'https://cdn.example.com/blogs/github-actions.jpg',
     '<h1>使用GitHub Actions实现CI/CD</h1><p>GitHub Actions提供了强大的CI/CD能力，本文将从零开始讲解如何配置...</p><h2>基本概念</h2><p>工作流、作业和步骤是Actions的三个核心概念...</p><h2>实战案例</h2><p>下面我们以一个Node.js项目为例，配置自动测试和部署...</p>',
     10, NOW() - INTERVAL 1 DAY, NOW());

-- 为点赞记录表插入模拟数据
-- 用户之间互相点赞
INSERT INTO thumb (userId, blogId, createTime) VALUES
                                                   (1, 3, NOW() - INTERVAL 4 DAY), -- 管理员点赞了johndoe的"全栈开发入门指南"
                                                   (1, 4, NOW() - INTERVAL 2 DAY), -- 管理员点赞了johndoe的"React vs Vue"
                                                   (1, 5, NOW() - INTERVAL 6 DAY), -- 管理员点赞了johndoe的"开发工具箱"
                                                   (1, 6, NOW() - INTERVAL 1 DAY), -- 管理员点赞了github用户的"开源项目贡献指南"
                                                   (1, 7, NOW() - INTERVAL 12 HOUR), -- 管理员点赞了github用户的"GitHub Actions实战教程"

                                                   (2, 1, NOW() - INTERVAL 9 DAY), -- johndoe点赞了管理员的"系统更新公告"
                                                   (2, 2, NOW() - INTERVAL 19 DAY), -- johndoe点赞了管理员的"社区管理规范"
                                                   (2, 6, NOW() - INTERVAL 2 DAY), -- johndoe点赞了github用户的"开源项目贡献指南"
                                                   (2, 7, NOW() - INTERVAL 18 HOUR), -- johndoe点赞了github用户的"GitHub Actions实战教程"

                                                   (3, 1, NOW() - INTERVAL 8 DAY), -- github用户点赞了管理员的"系统更新公告"
                                                   (3, 2, NOW() - INTERVAL 18 DAY), -- github用户点赞了管理员的"社区管理规范"
                                                   (3, 3, NOW() - INTERVAL 4 DAY), -- github用户点赞了johndoe的"全栈开发入门指南"
                                                   (3, 4, NOW() - INTERVAL 2 DAY), -- github用户点赞了johndoe的"React vs Vue"
                                                   (3, 5, NOW() - INTERVAL 6 DAY); -- github用户点赞了johndoe的"开发工具箱"

-- 如需更新博客文章的点赞数以匹配实际点赞记录数，可以运行以下SQL
UPDATE blog SET thumbCount = (SELECT COUNT(*) FROM thumb WHERE thumb.blogId = blog.id) WHERE id IN (1, 2, 3, 4, 5, 6, 7);

-- 添加新用户数据
INSERT INTO users (
    username, password, email, display_name, avatar_url, bio,
    status, email_verified, role, locale, timezone,
    createtime, updatetime, last_login_at,
    social_provider, social_id, metadata
) VALUES (
    'testuser1',
    'e10adc3949ba59abbe56e057f20f883e', -- MD5加密的"123456"
    'testuser1@example.com',
    '测试用户一号',
    'https://cdn.example.com/avatars/testuser1.png',
    '我是一个测试用户，喜欢写代码。',
    'ACTIVE',
    1,
    'USER',
    'zh-CN',
    'Asia/Shanghai',
    NOW() - INTERVAL 2 DAY,
    NOW(),
    NOW() - INTERVAL 2 HOUR,
    NULL,
    NULL,
    JSON_OBJECT('interests', 'coding, testing')
), (
    'testuser2',
    'e10adc3949ba59abbe56e057f20f883e', -- MD5加密的"123456"
    'testuser2@example.com',
    '测试用户二号',
    'https://cdn.example.com/avatars/testuser2.png',
    '另一个测试用户，专注于API测试。',
    'ACTIVE',
    1,
    'USER',
    'en-US',
    'America/New_York',
    NOW() - INTERVAL 1 DAY,
    NOW(),
    NOW() - INTERVAL 1 HOUR,
    NULL,
    NULL,
    JSON_OBJECT('specialty', 'API testing', 'experience', '3 years')
);

-- 假设新添加的 testuser1 的 id 是 6 (基于当前数据中用户数量为5)
-- 假设新添加的 testuser2 的 id 是 7

-- 为新用户和现有用户添加博客数据
INSERT INTO blog (userId, title, coverImg, content, thumbCount, createTime, updateTime) VALUES
    (6, '我的第一个博客', 'https://cdn.example.com/blogs/first-post.jpg',
     '<h1>欢迎来到我的博客！</h1><p>这是我使用GigaLike发布的第一篇博客。</p>',
     0, NOW() - INTERVAL 12 HOUR, NOW()),
    (6, '学习SQL的心得', 'https://cdn.example.com/blogs/sql-learning.png',
     '<h2>SQL学习之旅</h2><p>最近在学习SQL，感觉非常有趣且实用...</p>',
     0, NOW() - INTERVAL 6 HOUR, NOW()),
    (7, 'API测试的重要性', 'https://cdn.example.com/blogs/api-testing.jpg',
     '<h1>为什么API测试至关重要</h1><p>API是现代软件架构的核心组件，确保其可靠性非常关键...</p>',
     0, NOW() - INTERVAL 10 HOUR, NOW()),
    (2, '我最喜欢的编程语言', 'https://cdn.example.com/blogs/fav-lang.png', -- johndoe 的新博客
     '<h1>我最爱的编程语言探讨</h1><p>多年编程下来，我对不同语言有了自己的看法...</p>',
     0, NOW() - INTERVAL 1 DAY, NOW());

-- 假设新添加的博客 ID 如下：
-- "我的第一个博客" (testuser1) ID: 8
-- "学习SQL的心得" (testuser1) ID: 9
-- "API测试的重要性" (testuser2) ID: 10
-- "我最喜欢的编程语言" (johndoe) ID: 11

-- 添加新的点赞记录
INSERT INTO thumb (userId, blogId, createTime) VALUES
    (1, 8, NOW() - INTERVAL 5 HOUR),   -- admin 点赞 testuser1 的 "我的第一个博客"
    (2, 10, NOW() - INTERVAL 4 HOUR),  -- johndoe 点赞 testuser2 的 "API测试的重要性"
    (6, 1, NOW() - INTERVAL 3 HOUR),   -- testuser1 点赞 admin 的 "系统更新公告"
    (6, 7, NOW() - INTERVAL 2 HOUR),   -- testuser1 点赞 github_12345 的 "GitHub Actions实战教程"
    (7, 3, NOW() - INTERVAL 1 HOUR),   -- testuser2 点赞 johndoe 的 "全栈开发入门指南"
    (7, 11, NOW() - INTERVAL 30 MINUTE), -- testuser2 点赞 johndoe 的 "我最喜欢的编程语言"
    (3, 8, NOW() - INTERVAL 1 HOUR); -- github_12345 点赞 testuser1 的 "我的第一个博客"

-- 更新新博客文章的点赞数
-- 注意：这里的博客ID (8, 9, 10, 11) 是基于前面插入的假设。
-- 在实际应用中，应该使用 last_insert_id() 或其他机制获取真实的ID。
UPDATE blog SET thumbCount = (SELECT COUNT(*) FROM thumb WHERE thumb.blogId = blog.id) WHERE id IN (8, 9, 10, 11);

