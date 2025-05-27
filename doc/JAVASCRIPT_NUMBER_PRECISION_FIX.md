# JavaScript数字精度问题解决方案 🔢

## 问题描述

在前后端数据交互中，遇到了JavaScript数字精度丢失的问题：

**问题现象**：
- 后端返回的博客ID：`1926924684188102658`
- 前端解析到的ID：`1926924684188102700`（末尾数字发生变化）

**根本原因**：
JavaScript中的Number类型使用64位浮点数表示，但只能安全表示53位整数。当整数超过 `Number.MAX_SAFE_INTEGER = 2^53 - 1 = 9007199254740991` 时，就会发生精度丢失。

## 解决方案

### 方案一：集成到现有配置（推荐）

将Long类型序列化配置集成到现有的`WebMvcConfig`中：

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        // 时间模块配置
        JavaTimeModule timeModule = new JavaTimeModule();
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        timeModule.addSerializer(LocalDateTime.class, localDateTimeSerializer);

        // Long类型序列化模块配置（解决JavaScript数字精度问题）
        SimpleModule longModule = new SimpleModule();
        longModule.addSerializer(Long.class, ToStringSerializer.instance);
        longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        return Jackson2ObjectMapperBuilder.json()
                .modules(timeModule, longModule)  // 注册两个模块
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .timeZone(TimeZone.getTimeZone("Asia/Shanghai"))
                .build();
    }
    
    // 其他配置...
}
```

### 方案二：独立配置类

如果项目中没有现有的ObjectMapper配置，可以创建独立的配置类：

```java
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // 创建自定义模块
        SimpleModule module = new SimpleModule();
        
        // 将Long类型序列化为字符串，避免JavaScript精度丢失
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        
        // 注册模块
        objectMapper.registerModule(module);
        
        return objectMapper;
    }
}
```

**注意**：如果项目中已有ObjectMapper Bean，需要避免Bean名称冲突。推荐使用方案一。

## 实施效果

### 修改前
```json
{
  "code": 0,
  "data": {
    "id": 1926924684188102700,  // 精度丢失
    "userid": 1,
    "title": "测试26"
  }
}
```

### 修改后
```json
{
  "code": 0,
  "data": {
    "id": "1926924684188102658",  // 字符串形式，保持精度
    "userid": "1",
    "title": "测试26"
  }
}
```

## 前端处理

### JavaScript中的处理

```javascript
// 1. 接收数据时，ID已经是字符串
const blog = {
  id: "1926924684188102658",  // 字符串形式
  userid: "1",
  title: "测试26"
};

// 2. 比较ID时使用字符串比较
if (blog.id === "1926924684188102658") {
  console.log("ID匹配");
}

// 3. 如果需要数值运算，使用BigInt
const blogIdBigInt = BigInt(blog.id);
console.log(blogIdBigInt); // 1926924684188102658n

// 4. URL参数传递
const url = `/api/blog/get?blogId=${blog.id}`;
```

### TypeScript类型定义

```typescript
interface Blog {
  id: string;        // 改为string类型
  userid: string;    // 改为string类型
  title: string;
  coverImg?: string;
  content: string;
  thumbCount: number;
  createTime: string;
  updateTime: string;
}

interface ApiResponse<T> {
  code: number;
  data: T;
  message: string;
}
```

### React组件示例

```jsx
import React, { useState, useEffect } from 'react';

const BlogList = () => {
  const [blogs, setBlogs] = useState([]);

  useEffect(() => {
    fetch('/api/blog/list')
      .then(response => response.json())
      .then(data => {
        if (data.code === 0) {
          setBlogs(data.data.records);
        }
      });
  }, []);

  const handleBlogClick = (blogId) => {
    // blogId现在是字符串，可以安全使用
    console.log('点击博客ID:', blogId);
    // 跳转到博客详情页
    window.location.href = `/blog/${blogId}`;
  };

  return (
    <div>
      {blogs.map(blog => (
        <div key={blog.id} onClick={() => handleBlogClick(blog.id)}>
          <h3>{blog.title}</h3>
          <p>博客ID: {blog.id}</p>
        </div>
      ))}
    </div>
  );
};
```

### Vue组件示例

```vue
<template>
  <div>
    <div 
      v-for="blog in blogs" 
      :key="blog.id"
      @click="handleBlogClick(blog.id)"
    >
      <h3>{{ blog.title }}</h3>
      <p>博客ID: {{ blog.id }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      blogs: []
    };
  },
  async mounted() {
    try {
      const response = await this.$http.get('/blog/list');
      if (response.data.code === 0) {
        this.blogs = response.data.data.records;
      }
    } catch (error) {
      console.error('获取博客列表失败:', error);
    }
  },
  methods: {
    handleBlogClick(blogId) {
      // blogId现在是字符串，可以安全使用
      console.log('点击博客ID:', blogId);
      this.$router.push(`/blog/${blogId}`);
    }
  }
};
</script>
```

## 数据库查询处理

### MyBatis-Plus查询

```java
// 1. 根据字符串ID查询（MyBatis-Plus会自动转换）
@GetMapping("/get")
public BaseResponse<BlogVO> getBlogById(@RequestParam String blogId) {
    // 字符串会自动转换为Long进行数据库查询
    Long id = Long.valueOf(blogId);
    Blog blog = blogService.getById(id);
    return ResultUtils.success(blogService.getBlogVO(blog, request));
}

// 2. 批量查询
@PostMapping("/batch")
public BaseResponse<List<BlogVO>> getBlogsByIds(@RequestBody List<String> blogIds) {
    List<Long> ids = blogIds.stream()
        .map(Long::valueOf)
        .collect(Collectors.toList());
    
    List<Blog> blogs = blogService.listByIds(ids);
    return ResultUtils.success(blogService.getBlogVOList(blogs, request));
}
```

## 已处理的实体类

以下实体类已经添加了`@JsonSerialize(using = ToStringSerializer.class)`注解：

### 1. Blog实体类
- `id` - 博客ID
- `userid` - 用户ID

### 2. User实体类
- `id` - 用户ID

### 3. Topic实体类
- `id` - 话题ID
- `creatorId` - 创建者ID

### 4. BlogTopic实体类
- `id` - 关联记录ID
- `blogId` - 博客ID
- `topicId` - 话题ID

### 5. Thumb实体类
- `id` - 点赞记录ID
- `userId` - 用户ID
- `blogId` - 博客ID

## 验证方法

### 1. 后端验证

```java
@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/long-precision")
    public BaseResponse<Map<String, Object>> testLongPrecision() {
        Map<String, Object> result = new HashMap<>();
        
        // 测试大数值
        Long largeId = 1926924684188102658L;
        result.put("originalLong", largeId);
        result.put("maxSafeInteger", 9007199254740991L);
        result.put("isOverMaxSafe", largeId > 9007199254740991L);
        
        return ResultUtils.success(result);
    }
}
```

### 2. 前端验证

```javascript
// 测试数字精度
const testPrecision = () => {
  const largeNumber = 1926924684188102658;
  const maxSafeInteger = Number.MAX_SAFE_INTEGER;
  
  console.log('原始数字:', largeNumber);
  console.log('最大安全整数:', maxSafeInteger);
  console.log('是否超出安全范围:', largeNumber > maxSafeInteger);
  console.log('精度是否丢失:', largeNumber !== 1926924684188102658);
};

// 测试API返回
fetch('/api/test/long-precision')
  .then(response => response.json())
  .then(data => {
    console.log('API返回数据:', data);
    // 现在所有Long类型都应该是字符串
  });
```

## 注意事项

### 1. 数据类型一致性
- 前端接收到的ID都是字符串类型
- 需要更新前端的类型定义
- 比较操作使用字符串比较

### 2. URL参数处理
```javascript
// 正确的URL构建方式
const blogId = "1926924684188102658";
const url = `/api/blog/get?blogId=${blogId}`;

// 避免数值转换
// 错误：const url = `/api/blog/get?blogId=${Number(blogId)}`;
```

### 3. 存储和缓存
```javascript
// localStorage存储
localStorage.setItem('currentBlogId', blog.id); // 已经是字符串

// 从localStorage读取
const blogId = localStorage.getItem('currentBlogId'); // 字符串类型
```

### 4. 表单提交
```javascript
// 表单数据
const formData = {
  blogId: blog.id,  // 字符串类型
  title: 'new title',
  content: 'new content'
};

// 提交时保持字符串格式
fetch('/api/blog/update', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(formData)
});
```

## 总结

通过将Long类型序列化为字符串，我们成功解决了JavaScript数字精度丢失的问题。这种方案：

1. **彻底解决精度问题**：字符串形式完全保持数值精度
2. **向后兼容**：不影响现有的数据库操作
3. **前端友好**：避免了复杂的BigInt处理
4. **易于维护**：统一的处理方式，减少出错概率

建议在所有涉及大整数ID的项目中采用这种方案。 