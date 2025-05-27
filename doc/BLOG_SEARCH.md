# 博客搜索功能文档

## 功能概述

实现了博客搜索功能，支持对博客的标题、内容、话题进行模糊查找，并提供分页和排序功能。

## 接口详情

### 搜索博客

**接口地址：** `POST /api/blog/search`

**功能：** 根据关键词搜索博客，支持搜索标题、内容、话题

**请求参数：**
```json
{
  "keyword": "Java",
  "current": 1,
  "pageSize": 10,
  "sortField": "createTime",
  "sortOrder": "desc"
}
```

**参数说明：**
- `keyword`: 搜索关键词（必需），支持搜索标题、内容、话题
- `current`: 当前页码，默认为1
- `pageSize`: 每页大小，默认为10
- `sortField`: 排序字段，支持 `createTime`（创建时间）、`thumbCount`（点赞数），默认为 `createTime`
- `sortOrder`: 排序方式，支持 `asc`（升序）、`desc`（降序），默认为 `desc`

**响应示例：**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 123456,
        "title": "Java学习笔记",
        "content": "这是一篇关于Java基础的学习笔记...",
        "coverImg": "https://example.com/cover.jpg",
        "thumbCount": 25,
        "createTime": "2024-12-01T10:30:00",
        "updateTime": "2024-12-01T10:30:00",
        "hasThumb": false,
        "author": {
          "id": 789,
          "username": "developer",
          "displayName": "开发者",
          "avatarUrl": "https://example.com/avatar.jpg"
        },
        "topics": [
          {
            "id": 1,
            "name": "Java",
            "description": "Java编程语言",
            "color": "#FF6B35"
          },
          {
            "id": 2,
            "name": "编程学习",
            "description": "编程学习相关",
            "color": "#4ECDC4"
          }
        ]
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

## 搜索范围

### 1. 标题搜索
- 对博客标题进行模糊匹配
- 例如：搜索"Java"会匹配标题包含"Java"的博客

### 2. 内容搜索
- 对博客正文内容进行模糊匹配
- 例如：搜索"Spring Boot"会匹配内容包含"Spring Boot"的博客

### 3. 话题搜索
- 对博客关联的话题名称进行模糊匹配
- 例如：搜索"前端"会匹配包含"前端开发"、"前端框架"等话题的博客

## 使用示例

### 1. 基础搜索

```bash
curl -X POST http://localhost:8123/api/blog/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Java"
  }'
```

### 2. 分页搜索

```bash
curl -X POST http://localhost:8123/api/blog/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Spring Boot",
    "current": 2,
    "pageSize": 5
  }'
```

### 3. 按点赞数排序

```bash
curl -X POST http://localhost:8123/api/blog/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "React",
    "sortField": "thumbCount",
    "sortOrder": "desc"
  }'
```

### 4. 前端集成示例

#### JavaScript/Fetch
```javascript
async function searchBlogs(keyword, page = 1, pageSize = 10) {
  try {
    const response = await fetch('/api/blog/search', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        keyword: keyword,
        current: page,
        pageSize: pageSize,
        sortField: 'createTime',
        sortOrder: 'desc'
      })
    });

    const result = await response.json();
    
    if (result.code === 0) {
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('搜索失败:', error);
    throw error;
  }
}

// 使用示例
searchBlogs('Java', 1, 10)
  .then(data => {
    console.log('搜索结果:', data);
    // 处理搜索结果
  })
  .catch(error => {
    console.error('搜索出错:', error);
  });
```

#### Vue.js
```vue
<template>
  <div class="blog-search">
    <div class="search-box">
      <input 
        v-model="searchKeyword" 
        @keyup.enter="handleSearch"
        placeholder="搜索博客标题、内容或话题..."
        class="search-input"
      />
      <button @click="handleSearch" class="search-btn">搜索</button>
    </div>

    <div class="search-options">
      <select v-model="sortField" @change="handleSearch">
        <option value="createTime">按时间排序</option>
        <option value="thumbCount">按点赞数排序</option>
      </select>
      
      <select v-model="sortOrder" @change="handleSearch">
        <option value="desc">降序</option>
        <option value="asc">升序</option>
      </select>
    </div>

    <div class="search-results" v-if="searchResults.records">
      <div 
        v-for="blog in searchResults.records" 
        :key="blog.id"
        class="blog-item"
      >
        <h3>{{ blog.title }}</h3>
        <p>{{ blog.content.substring(0, 200) }}...</p>
        <div class="blog-meta">
          <span>作者: {{ blog.author.displayName }}</span>
          <span>点赞: {{ blog.thumbCount }}</span>
          <span>时间: {{ formatDate(blog.createTime) }}</span>
        </div>
        <div class="blog-topics">
          <span 
            v-for="topic in blog.topics" 
            :key="topic.id"
            class="topic-tag"
            :style="{ backgroundColor: topic.color }"
          >
            {{ topic.name }}
          </span>
        </div>
      </div>
    </div>

    <div class="pagination" v-if="searchResults.total > 0">
      <button 
        @click="changePage(searchResults.current - 1)"
        :disabled="searchResults.current <= 1"
      >
        上一页
      </button>
      
      <span>
        第 {{ searchResults.current }} 页，共 {{ searchResults.pages }} 页
      </span>
      
      <button 
        @click="changePage(searchResults.current + 1)"
        :disabled="searchResults.current >= searchResults.pages"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchKeyword: '',
      sortField: 'createTime',
      sortOrder: 'desc',
      currentPage: 1,
      pageSize: 10,
      searchResults: {},
      loading: false
    };
  },
  methods: {
    async handleSearch() {
      if (!this.searchKeyword.trim()) {
        this.$message.warning('请输入搜索关键词');
        return;
      }

      this.loading = true;
      this.currentPage = 1;

      try {
        const response = await this.$http.post('/api/blog/search', {
          keyword: this.searchKeyword,
          current: this.currentPage,
          pageSize: this.pageSize,
          sortField: this.sortField,
          sortOrder: this.sortOrder
        });

        if (response.data.code === 0) {
          this.searchResults = response.data.data;
        } else {
          this.$message.error(response.data.message);
        }
      } catch (error) {
        console.error('搜索失败:', error);
        this.$message.error('搜索失败');
      } finally {
        this.loading = false;
      }
    },

    async changePage(page) {
      if (page < 1 || page > this.searchResults.pages) {
        return;
      }

      this.currentPage = page;
      this.loading = true;

      try {
        const response = await this.$http.post('/api/blog/search', {
          keyword: this.searchKeyword,
          current: this.currentPage,
          pageSize: this.pageSize,
          sortField: this.sortField,
          sortOrder: this.sortOrder
        });

        if (response.data.code === 0) {
          this.searchResults = response.data.data;
        }
      } catch (error) {
        console.error('翻页失败:', error);
      } finally {
        this.loading = false;
      }
    },

    formatDate(dateString) {
      return new Date(dateString).toLocaleString();
    }
  }
};
</script>

<style scoped>
.blog-search {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.search-box {
  display: flex;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
}

.search-btn {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.search-options {
  margin-bottom: 20px;
}

.search-options select {
  margin-right: 10px;
  padding: 5px;
}

.blog-item {
  border: 1px solid #eee;
  padding: 15px;
  margin-bottom: 15px;
  border-radius: 4px;
}

.blog-meta {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
}

.blog-meta span {
  margin-right: 15px;
}

.blog-topics {
  margin-top: 10px;
}

.topic-tag {
  display: inline-block;
  padding: 2px 8px;
  margin-right: 5px;
  border-radius: 12px;
  color: white;
  font-size: 12px;
}

.pagination {
  text-align: center;
  margin-top: 20px;
}

.pagination button {
  margin: 0 10px;
  padding: 5px 15px;
}
</style>
```

## 性能优化建议

### 1. 数据库索引
建议为以下字段添加索引以提高搜索性能：

```sql
-- 博客表索引
CREATE INDEX idx_blog_title ON blog(title);
CREATE INDEX idx_blog_content ON blog(content(100)); -- 对内容字段的前100个字符建索引
CREATE INDEX idx_blog_create_time ON blog(create_time);
CREATE INDEX idx_blog_thumb_count ON blog(thumb_count);

-- 话题表索引
CREATE INDEX idx_topic_name ON topics(name);

-- 博客话题关联表索引
CREATE INDEX idx_blog_topic_blog_id ON blog_topics(blog_id);
CREATE INDEX idx_blog_topic_topic_id ON blog_topics(topic_id);
```

### 2. 搜索优化
- 对于大量数据，考虑使用全文搜索引擎（如Elasticsearch）
- 实现搜索结果缓存
- 添加搜索关键词高亮显示

### 3. 前端优化
- 实现搜索防抖，避免频繁请求
- 添加搜索历史记录
- 实现搜索建议/自动完成

## 错误处理

### 常见错误码
- `搜索请求不能为空`: 请求体为空
- `搜索关键词不能为空`: keyword字段为空或空字符串

### 容错机制
- 自动过滤无效的排序字段
- 默认排序方式处理
- 分页参数验证

博客搜索功能已完全实现并可投入使用！ 