# 话题功能使用指南 🏷️

## 概述

本项目实现了完整的话题标签功能，类似于抖音、小红书等平台的话题系统。用户可以为博客文章添加话题标签，关注感兴趣的话题，发现相关内容。

## 功能特性

### 🎯 核心功能
- ✅ **话题创建**：用户可以创建新的话题
- ✅ **话题关联**：博客文章可以关联最多10个话题标签
- ✅ **话题关注**：用户可以关注感兴趣的话题
- ✅ **话题搜索**：支持话题名称和描述的模糊搜索
- ✅ **热门话题**：按帖子数量、关注数量等维度排序
- ✅ **话题统计**：自动维护话题的帖子数量和关注数量

### 🎨 设计特色
- ✅ **多对多关系**：博客与话题之间的灵活关联
- ✅ **分布式友好**：支持Redis Session的分布式部署
- ✅ **性能优化**：批量查询，减少数据库访问
- ✅ **数据一致性**：使用数据库触发器自动维护计数
- ✅ **扩展性强**：支持话题热度统计等高级功能

## 数据库设计

### 设计原则

**⚠️ 重要说明：本项目不使用数据库外键约束，所有关联关系通过程序逻辑约束维护**

- **性能优化**：避免外键约束带来的性能开销
- **灵活性**：便于数据迁移和分库分表
- **可控性**：通过程序逻辑精确控制数据一致性
- **扩展性**：支持更复杂的业务逻辑和异步处理

### 表结构

#### 1. 话题表 (topics)
```sql
CREATE TABLE topics (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,           -- 话题名称
    description TEXT,                            -- 话题描述
    cover_image VARCHAR(255),                    -- 话题封面
    color VARCHAR(7) DEFAULT '#1890ff',          -- 主题色
    status ENUM('active', 'inactive', 'banned'), -- 状态
    post_count INT UNSIGNED DEFAULT 0,           -- 帖子数量
    follow_count INT UNSIGNED DEFAULT 0,         -- 关注数量
    creator_id BIGINT UNSIGNED,                  -- 创建者ID（逻辑关联users表）
    is_official BOOLEAN DEFAULT FALSE,           -- 是否官方话题
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引优化
    UNIQUE KEY uk_name (name),
    INDEX idx_status (status),
    INDEX idx_post_count (post_count),
    INDEX idx_creator_id (creator_id),
    INDEX idx_create_time (create_time)
);
```

#### 2. 博客话题关联表 (blog_topics)
```sql
CREATE TABLE blog_topics (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    blog_id BIGINT UNSIGNED NOT NULL,            -- 博客ID（逻辑关联blog表）
    topic_id BIGINT UNSIGNED NOT NULL,           -- 话题ID（逻辑关联topics表）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引优化
    UNIQUE KEY uk_blog_topic (blog_id, topic_id),
    INDEX idx_blog_id (blog_id),
    INDEX idx_topic_id (topic_id),
    INDEX idx_create_time (create_time)
);
```

#### 3. 用户话题关注表 (user_topic_follows)
```sql
CREATE TABLE user_topic_follows (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,            -- 用户ID（逻辑关联users表）
    topic_id BIGINT UNSIGNED NOT NULL,           -- 话题ID（逻辑关联topics表）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引优化
    UNIQUE KEY uk_user_topic (user_id, topic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_topic_id (topic_id),
    INDEX idx_create_time (create_time)
);
```

### 触发器
系统使用数据库触发器自动维护话题的统计数据：
- 当博客关联话题时，自动增加话题的帖子数量
- 当用户关注话题时，自动增加话题的关注数量

### 程序逻辑约束

由于不使用数据库外键约束，系统通过以下程序逻辑确保数据一致性：

#### 1. 数据完整性检查
```java
// 创建博客话题关联前，检查博客和话题是否存在
@Transactional(rollbackFor = Exception.class)
public Long createBlog(BlogCreateRequest request, HttpServletRequest httpRequest) {
    // 验证话题ID的有效性
    if (request.getTopicIds() != null) {
        for (Long topicId : request.getTopicIds()) {
            Topic topic = topicService.getById(topicId);
            if (topic == null || !"active".equals(topic.getStatus())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "话题不存在或已禁用");
            }
        }
    }
    // ... 创建博客和关联逻辑
}
```

#### 2. 级联删除处理
```java
// 删除博客时，同时删除相关的话题关联
@Transactional(rollbackFor = Exception.class)
public Boolean deleteBlog(Long blogId, HttpServletRequest request) {
    // 删除博客话题关联
    blogTopicMapper.deleteByBlogId(blogId);
    // 删除博客
    return this.removeById(blogId);
}
```

#### 3. 数据一致性维护
```java
// 定期校验和修复统计数据
@Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
public void repairTopicStatistics() {
    List<Topic> topics = topicService.list();
    for (Topic topic : topics) {
        // 重新计算帖子数量
        long actualPostCount = blogTopicMapper.selectCount(
            new QueryWrapper<BlogTopic>().eq("topic_id", topic.getId())
        );
        // 重新计算关注数量
        long actualFollowCount = userTopicFollowMapper.selectCount(
            new QueryWrapper<UserTopicFollow>().eq("topic_id", topic.getId())
        );
        
        // 更新统计数据
        if (topic.getPostCount() != actualPostCount || 
            topic.getFollowCount() != actualFollowCount) {
            topic.setPostCount((int) actualPostCount);
            topic.setFollowCount((int) actualFollowCount);
            topicService.updateById(topic);
        }
    }
}
```

#### 4. 话题自动创建机制
```java
// 根据话题名称获取或创建话题
@Transactional(rollbackFor = Exception.class)
public Topic getOrCreateTopicByName(String topicName, HttpServletRequest request) {
    // 1. 参数验证和清理
    topicName = topicName.trim();
    if (topicName.length() > 100) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "话题名称不能超过100个字符");
    }
    
    // 2. 查找现有话题
    Topic existingTopic = this.getOne(new QueryWrapper<Topic>().eq("name", topicName));
    if (existingTopic != null) {
        return existingTopic;
    }
    
    // 3. 自动创建新话题
    Topic newTopic = new Topic();
    newTopic.setName(topicName);
    newTopic.setDescription("用户创建的话题：" + topicName);
    newTopic.setColor("#1890ff"); // 默认颜色
    newTopic.setStatus("active");
    newTopic.setIsOfficial(false); // 用户创建的话题默认不是官方话题
    
    // 4. 设置创建者（登录用户或系统默认）
    User loginUser = getCurrentUserOrDefault(request);
    newTopic.setCreatorId(loginUser.getId());
    
    this.save(newTopic);
    return newTopic;
}
```

**话题自动创建规则**：
- 话题名称自动去除前后空格
- 话题名称不能超过100个字符
- 新创建的话题默认为非官方话题
- 如果用户未登录，使用系统管理员作为创建者
- 新话题默认状态为活跃（active）
- 自动生成描述："用户创建的话题：{话题名称}"

#### 5. 批量操作优化
```java
// 批量插入博客话题关联，避免循环插入
@Transactional(rollbackFor = Exception.class)
public Long createBlog(BlogCreateRequest request, HttpServletRequest httpRequest) {
    // ... 创建博客逻辑
    
    if (topicNames != null && !topicNames.isEmpty()) {
        // 根据话题名称获取或创建话题
        List<Topic> topics = topicService.getOrCreateTopicsByNames(topicNames, request);
        
        // 构建批量插入数据
        List<BlogTopic> blogTopics = topics.stream()
                .map(topic -> {
                    BlogTopic blogTopic = new BlogTopic();
                    blogTopic.setBlogId(blog.getId());
                    blogTopic.setTopicId(topic.getId());
                    blogTopic.setCreateTime(new Date());
                    return blogTopic;
                })
                .collect(Collectors.toList());
        
        // 一次性批量插入，避免循环操作数据库
        if (!blogTopics.isEmpty()) {
            blogTopicMapper.batchInsert(blogTopics);
        }
    }
}
```

**批量操作优势**：
- **性能提升**：一次SQL执行插入多条记录，减少数据库连接开销
- **事务安全**：所有关联记录在同一事务中处理，保证数据一致性
- **资源节约**：减少网络往返次数和数据库锁竞争
- **扩展性好**：支持大量话题关联的高效处理

## API 接口

### 话题管理

#### 1. 创建话题
```http
POST /topic/create
Authorization: Required
Content-Type: application/json

{
    "name": "前端开发",
    "description": "前端技术讨论，包括HTML、CSS、JavaScript等",
    "coverImage": "https://example.com/cover.jpg",
    "color": "#61dafb"
}
```

#### 2. 获取话题详情
```http
GET /topic/get?topicId=1
```

#### 3. 分页查询话题
```http
POST /topic/list
Content-Type: application/json

{
    "name": "开发",
    "status": "active",
    "isOfficial": true,
    "sortField": "post_count",
    "sortOrder": "desc",
    "current": 1,
    "pageSize": 10
}
```

#### 4. 搜索话题
```http
GET /topic/search?keyword=前端
```

#### 5. 获取热门话题
```http
GET /topic/hot?limit=10
```

#### 6. 关注/取消关注话题
```http
POST /topic/follow?topicId=1
POST /topic/unfollow?topicId=1
Authorization: Required
```

#### 7. 获取用户关注的话题
```http
GET /topic/followed
Authorization: Required
```

### 博客管理

#### 1. 创建博客（包含话题）
```http
POST /blog/create
Authorization: Required
Content-Type: application/json

{
    "title": "我的第一篇博客",
    "content": "这是博客内容...",
    "coverImg": "https://example.com/cover.jpg",
    "topicNames": ["前端开发", "React", "新手教程"]
}
```

**注意**：
- 使用话题名称而不是ID
- 如果话题不存在，系统会自动创建
- 最多支持10个话题
- 话题名称会自动去重和去除空格

#### 2. 更新博客话题
```http
POST /blog/topics/update?blogId=1
Authorization: Required
Content-Type: application/json

["前端开发", "Vue", "进阶教程", "新话题"]
```

**注意**：
- 支持话题名称，不存在的话题会自动创建
- 会完全替换原有的话题关联

#### 3. 获取博客详情（包含话题）
```http
GET /blog/get?blogId=1
```

## 使用示例

### 前端集成示例

#### 1. 话题选择器组件
```javascript
// 获取热门话题
const getHotTopics = async () => {
    const response = await fetch('/topic/hot?limit=20');
    const result = await response.json();
    return result.data;
};

// 搜索话题
const searchTopics = async (keyword) => {
    const response = await fetch(`/topic/search?keyword=${keyword}`);
    const result = await response.json();
    return result.data;
};

// 创建博客时选择话题
const createBlog = async (blogData) => {
    const response = await fetch('/blog/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            title: blogData.title,
            content: blogData.content,
            topicNames: blogData.selectedTopics // 最多10个话题名称，支持自动创建
        })
    });
    return response.json();
};

// 话题输入组件示例
const TopicInput = ({ value, onChange }) => {
    const [inputValue, setInputValue] = useState('');
    const [suggestions, setSuggestions] = useState([]);

    // 搜索话题建议
    const searchTopics = async (keyword) => {
        if (keyword.trim()) {
            const response = await fetch(`/topic/search?keyword=${keyword}`);
            const result = await response.json();
            setSuggestions(result.data || []);
        } else {
            setSuggestions([]);
        }
    };

    // 添加话题
    const addTopic = (topicName) => {
        if (topicName && !value.includes(topicName) && value.length < 10) {
            onChange([...value, topicName]);
            setInputValue('');
            setSuggestions([]);
        }
    };

    return (
        <div className="topic-input">
            <input
                type="text"
                value={inputValue}
                onChange={(e) => {
                    setInputValue(e.target.value);
                    searchTopics(e.target.value);
                }}
                onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        addTopic(inputValue.trim());
                    }
                }}
                placeholder="输入话题名称，按回车添加"
            />
            
            {/* 话题建议列表 */}
            {suggestions.length > 0 && (
                <div className="suggestions">
                    {suggestions.map(topic => (
                        <div 
                            key={topic.id} 
                            onClick={() => addTopic(topic.name)}
                            className="suggestion-item"
                        >
                            #{topic.name}
                        </div>
                    ))}
                </div>
            )}
            
            {/* 已选择的话题 */}
            <div className="selected-topics">
                {value.map((topicName, index) => (
                    <span key={index} className="topic-tag">
                        #{topicName}
                        <button onClick={() => {
                            const newValue = value.filter((_, i) => i !== index);
                            onChange(newValue);
                        }}>×</button>
                    </span>
                ))}
            </div>
        </div>
    );
};
```

#### 2. 话题展示组件
```javascript
// 话题标签组件
const TopicTag = ({ topic }) => (
    <span 
        className="topic-tag" 
        style={{ backgroundColor: topic.color }}
    >
        #{topic.name}
    </span>
);

// 博客话题列表
const BlogTopics = ({ topics }) => (
    <div className="blog-topics">
        {topics.map(topic => (
            <TopicTag key={topic.id} topic={topic} />
        ))}
    </div>
);
```

### 后端扩展示例

#### 1. 自定义话题查询
```java
// 根据话题获取相关博客
@GetMapping("/topic/{topicId}/blogs")
public BaseResponse<List<BlogVO>> getBlogsByTopic(
    @PathVariable Long topicId,
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int size,
    HttpServletRequest request) {
    
    // 实现分页查询该话题下的博客
    Page<Blog> blogPage = new Page<>(page, size);
    QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
    queryWrapper.inSql("id", 
        "SELECT blog_id FROM blog_topics WHERE topic_id = " + topicId);
    queryWrapper.orderByDesc("create_time");
    
    Page<Blog> result = blogService.page(blogPage, queryWrapper);
    List<BlogVO> blogVOList = blogService.getBlogVOList(result.getRecords(), request);
    
    return ResultUtils.success(blogVOList);
}
```

#### 2. 话题推荐算法
```java
// 基于用户关注的话题推荐相关话题
@GetMapping("/topic/recommend")
@AuthCheck(mustLogin = true)
public BaseResponse<List<TopicVO>> getRecommendedTopics(HttpServletRequest request) {
    User loginUser = userService.getLoginUser(request);
    
    // 获取用户关注的话题
    List<Topic> followedTopics = topicMapper.selectFollowedTopicsByUserId(loginUser.getId());
    
    // 基于关注的话题推荐相关话题（可以根据话题描述的相似度等算法实现）
    // 这里简化为推荐热门话题
    List<Topic> recommendedTopics = topicMapper.selectHotTopics(10);
    
    return ResultUtils.success(topicService.getTopicVOList(recommendedTopics, request));
}
```

## 最佳实践

### 1. 话题命名规范
- 使用简洁明了的名称
- 避免重复和相似的话题
- 官方话题使用统一的命名规范

### 2. 性能优化
- 使用Redis缓存热门话题
- 批量查询减少数据库访问
- 合理使用索引提升查询性能

### 3. 用户体验
- 提供话题搜索和推荐功能
- 限制每篇博客的话题数量（最多10个）
- 支持话题的颜色主题定制

### 4. 数据一致性
- 使用数据库事务保证数据一致性
- 定期校验和修复统计数据
- 监控话题数据的异常变化

## 扩展功能

### 1. 话题热度分析
- 实现话题热度统计表
- 分析话题的时间趋势
- 提供话题热度排行榜

### 2. 智能推荐
- 基于用户行为推荐话题
- 根据内容相似度推荐相关话题
- 个性化话题推荐算法

### 3. 话题管理
- 管理员话题审核功能
- 话题合并和重定向
- 违规话题处理机制

## 注意事项

1. **数据库性能**：大量话题关联可能影响查询性能，需要合理设计索引
2. **缓存策略**：热门话题和用户关注话题适合缓存
3. **权限控制**：话题创建和管理需要适当的权限控制
4. **数据清理**：定期清理无效的话题关联数据

## 总结

话题功能为博客系统提供了强大的内容组织和发现能力，通过合理的数据库设计和API接口，可以支持类似主流社交平台的话题功能。系统具有良好的扩展性和性能，可以根据业务需求进行进一步的功能增强。 