# 博客列表话题筛选功能说明 📝

## 功能概述

博客列表接口现在支持根据话题进行筛选，用户可以通过传递话题ID参数来获取特定话题下的博客列表。这是一个可选功能，不传递话题参数时返回所有博客。

## 接口详情

### 接口地址
```
GET /api/blog/list
```

### 请求参数

| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型 | 示例值 |
|---------|---------|---------|---------|---------|--------|
| topicId | 话题ID   | query   | false   | Long    | 1001   |

### 使用场景

#### 1. 获取所有博客列表（默认行为）

**请求示例：**
```http
GET /api/blog/list
```

**说明：** 不传递任何参数时，返回所有博客的列表。

#### 2. 根据话题筛选博客列表

**请求示例：**
```http
GET /api/blog/list?topicId=1001
```

**说明：** 传递话题ID时，只返回包含该话题的博客列表。

### 响应格式

```json
{
    "code": 0,
    "message": "success",
    "data": [
        {
            "id": 1,
            "title": "Spring Boot 入门教程",
            "coverImg": "https://example.com/cover.jpg",
            "content": "这是一篇关于Spring Boot的入门教程...",
            "thumbCount": 15,
            "createTime": "2025-01-27T10:30:00",
            "hasThumb": false,
            "author": {
                "id": 1001,
                "username": "developer",
                "displayName": "开发者",
                "avatarUrl": "https://example.com/avatar.jpg"
            },
            "topics": [
                {
                    "id": 1001,
                    "name": "Spring Boot",
                    "description": "Spring Boot框架相关内容",
                    "color": "#007bff",
                    "postCount": 25,
                    "isFollowed": false
                }
            ]
        }
    ]
}
```

## 技术实现

### 1. 控制器层修改

```java
@GetMapping("/list")
@Operation(summary = "获取博客列表", description = "获取博客列表，支持根据话题筛选，包含话题标签信息")
public BaseResponse<List<BlogVO>> list(@RequestParam(required = false) Long topicId,
                                      HttpServletRequest request) {
    List<Blog> blogList;
    if (topicId != null) {
        // 根据话题查询博客列表
        blogList = blogService.getBlogsByTopicId(topicId);
    } else {
        // 查询所有博客
        blogList = blogService.list();
    }
    List<BlogVO> blogVOList = blogService.getBlogVOList(blogList, request);
    return ResultUtils.success(blogVOList);
}
```

### 2. 服务层新增方法

```java
/**
 * 根据话题ID查询博客列表
 */
List<Blog> getBlogsByTopicId(Long topicId);
```

### 3. 数据访问层查询

```java
/**
 * 根据话题ID查询博客列表
 */
@Select("SELECT b.* FROM blog b " +
        "JOIN blog_topics bt ON b.id = bt.blog_id " +
        "WHERE bt.topic_id = #{topicId} " +
        "ORDER BY b.create_time DESC")
List<Blog> selectBlogsByTopicId(@Param("topicId") Long topicId);
```

### 4. 数据库查询逻辑

- 通过 `blog_topics` 关联表连接 `blog` 表和 `topics` 表
- 根据传入的 `topicId` 筛选博客
- 按创建时间倒序排列，最新的博客在前

## 前端集成示例

### React 示例

```jsx
import React, { useState, useEffect } from 'react';

const BlogList = () => {
    const [blogs, setBlogs] = useState([]);
    const [selectedTopicId, setSelectedTopicId] = useState(null);
    const [loading, setLoading] = useState(false);

    // 获取博客列表
    const fetchBlogs = async (topicId = null) => {
        setLoading(true);
        try {
            const url = topicId 
                ? `/api/blog/list?topicId=${topicId}`
                : '/api/blog/list';
            
            const response = await fetch(url);
            const result = await response.json();
            
            if (result.code === 0) {
                setBlogs(result.data);
            }
        } catch (error) {
            console.error('获取博客列表失败:', error);
        } finally {
            setLoading(false);
        }
    };

    // 初始加载
    useEffect(() => {
        fetchBlogs();
    }, []);

    // 话题筛选
    const handleTopicFilter = (topicId) => {
        setSelectedTopicId(topicId);
        fetchBlogs(topicId);
    };

    // 清除筛选
    const clearFilter = () => {
        setSelectedTopicId(null);
        fetchBlogs();
    };

    return (
        <div className="blog-list">
            <div className="filter-bar">
                <button 
                    onClick={clearFilter}
                    className={!selectedTopicId ? 'active' : ''}
                >
                    全部
                </button>
                {/* 话题筛选按钮 */}
                <button 
                    onClick={() => handleTopicFilter(1001)}
                    className={selectedTopicId === 1001 ? 'active' : ''}
                >
                    Spring Boot
                </button>
                <button 
                    onClick={() => handleTopicFilter(1002)}
                    className={selectedTopicId === 1002 ? 'active' : ''}
                >
                    React
                </button>
            </div>

            {loading ? (
                <div>加载中...</div>
            ) : (
                <div className="blog-grid">
                    {blogs.map(blog => (
                        <div key={blog.id} className="blog-card">
                            <h3>{blog.title}</h3>
                            <p>{blog.content.substring(0, 100)}...</p>
                            <div className="blog-meta">
                                <span>👍 {blog.thumbCount}</span>
                                <span>📅 {blog.createTime}</span>
                            </div>
                            <div className="blog-topics">
                                {blog.topics.map(topic => (
                                    <span 
                                        key={topic.id} 
                                        className="topic-tag"
                                        style={{ backgroundColor: topic.color }}
                                        onClick={() => handleTopicFilter(topic.id)}
                                    >
                                        {topic.name}
                                    </span>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default BlogList;
```

### Vue 示例

```vue
<template>
  <div class="blog-list">
    <div class="filter-bar">
      <button 
        @click="clearFilter"
        :class="{ active: !selectedTopicId }"
      >
        全部
      </button>
      <button 
        @click="handleTopicFilter(1001)"
        :class="{ active: selectedTopicId === 1001 }"
      >
        Spring Boot
      </button>
      <button 
        @click="handleTopicFilter(1002)"
        :class="{ active: selectedTopicId === 1002 }"
      >
        React
      </button>
    </div>

    <div v-if="loading">加载中...</div>
    <div v-else class="blog-grid">
      <div 
        v-for="blog in blogs" 
        :key="blog.id" 
        class="blog-card"
      >
        <h3>{{ blog.title }}</h3>
        <p>{{ blog.content.substring(0, 100) }}...</p>
        <div class="blog-meta">
          <span>👍 {{ blog.thumbCount }}</span>
          <span>📅 {{ blog.createTime }}</span>
        </div>
        <div class="blog-topics">
          <span 
            v-for="topic in blog.topics"
            :key="topic.id"
            class="topic-tag"
            :style="{ backgroundColor: topic.color }"
            @click="handleTopicFilter(topic.id)"
          >
            {{ topic.name }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BlogList',
  data() {
    return {
      blogs: [],
      selectedTopicId: null,
      loading: false
    };
  },
  mounted() {
    this.fetchBlogs();
  },
  methods: {
    async fetchBlogs(topicId = null) {
      this.loading = true;
      try {
        const url = topicId 
          ? `/api/blog/list?topicId=${topicId}`
          : '/api/blog/list';
        
        const response = await this.$http.get(url);
        
        if (response.data.code === 0) {
          this.blogs = response.data.data;
        }
      } catch (error) {
        console.error('获取博客列表失败:', error);
        this.$message.error('获取博客列表失败');
      } finally {
        this.loading = false;
      }
    },
    handleTopicFilter(topicId) {
      this.selectedTopicId = topicId;
      this.fetchBlogs(topicId);
    },
    clearFilter() {
      this.selectedTopicId = null;
      this.fetchBlogs();
    }
  }
};
</script>
```

## 性能优化建议

### 1. 数据库索引

确保在 `blog_topics` 表上创建合适的索引：

```sql
-- 为话题ID创建索引，提高查询性能
CREATE INDEX idx_blog_topics_topic_id ON blog_topics(topic_id);

-- 为博客ID创建索引，用于关联查询
CREATE INDEX idx_blog_topics_blog_id ON blog_topics(blog_id);

-- 复合索引，进一步优化查询
CREATE INDEX idx_blog_topics_topic_blog ON blog_topics(topic_id, blog_id);
```

### 2. 缓存策略

```java
@Cacheable(value = "blogsByTopic", key = "#topicId")
public List<Blog> getBlogsByTopicId(Long topicId) {
    if (topicId == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "话题ID不能为空");
    }
    return this.baseMapper.selectBlogsByTopicId(topicId);
}
```

### 3. 分页支持

考虑为博客列表添加分页功能：

```java
@GetMapping("/list")
public BaseResponse<Page<BlogVO>> list(
    @RequestParam(required = false) Long topicId,
    @RequestParam(defaultValue = "1") Integer current,
    @RequestParam(defaultValue = "10") Integer pageSize,
    HttpServletRequest request) {
    // 分页查询实现
}
```

## 错误处理

### 常见错误及处理

1. **话题ID不存在**
   ```json
   {
       "code": 40400,
       "message": "话题不存在",
       "data": null
   }
   ```

2. **参数格式错误**
   ```json
   {
       "code": 40000,
       "message": "话题ID格式错误",
       "data": null
   }
   ```

## 测试用例

### 1. 功能测试

```bash
# 测试获取所有博客
curl -X GET "http://localhost:8123/api/blog/list"

# 测试根据话题筛选
curl -X GET "http://localhost:8123/api/blog/list?topicId=1001"

# 测试无效话题ID
curl -X GET "http://localhost:8123/api/blog/list?topicId=99999"
```

### 2. 性能测试

```bash
# 使用ab工具进行压力测试
ab -n 1000 -c 10 "http://localhost:8123/api/blog/list?topicId=1001"
```

## 总结

博客列表话题筛选功能为用户提供了更精准的内容浏览体验，通过简单的参数传递即可实现话题筛选。该功能具有以下特点：

1. **向后兼容**：不传递参数时保持原有行为
2. **性能优化**：通过数据库索引和缓存提高查询效率
3. **用户友好**：支持前端灵活的交互设计
4. **扩展性强**：为后续添加更多筛选条件奠定基础

这个功能增强了博客系统的内容组织和发现能力，提升了用户体验。 