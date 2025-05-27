# 图片上传功能使用指南 📸

## 概述

本项目集成了腾讯云COS（对象存储）服务，支持博客图片的上传、存储和管理。图片上传采用智能路径管理，确保文件的有序存储和高效访问。

## 功能特性

### 🎯 核心功能
- ✅ **图片上传**：支持jpg、png、gif、webp格式
- ✅ **智能路径**：按用户ID、博客ID、标题自动组织文件结构
- ✅ **文件安全**：文件类型验证、大小限制（10MB）
- ✅ **路径更新**：博客创建后自动更新临时路径为正式路径
- ✅ **文件管理**：支持文件删除和复制操作

### 🎨 设计特色
- ✅ **唯一文件名**：基于时间戳和UUID生成，避免冲突
- ✅ **分层存储**：`/userId/blogId/title/fileName`的清晰目录结构
- ✅ **临时机制**：创建博客前使用临时目录，创建后自动迁移
- ✅ **CDN支持**：支持自定义域名加速访问

## 配置说明

### 1. 环境变量配置

在系统环境变量或`.env`文件中配置：

```bash
# 腾讯云COS配置
COS_SECRET_ID=your-secret-id
COS_SECRET_KEY=your-secret-key
COS_DOMAIN=https://your-custom-domain.com  # 可选，自定义域名
```

### 2. application.yml配置

```yaml
# 腾讯云COS配置
cos:
  secret-id: ${COS_SECRET_ID:your-secret-id}
  secret-key: ${COS_SECRET_KEY:your-secret-key}
  bucket-name: gigalike-1307454348
  region: ap-beijing
  domain: ${COS_DOMAIN:} # 可选，自定义域名

# Spring文件上传配置
spring:
  servlet:
    multipart:
      max-file-size: 10MB     # 单个文件最大10MB
      max-request-size: 20MB  # 请求最大20MB
```

## 文件存储结构

### 目录组织

```
gigalike-1307454348/
├── 1001/                    # 用户ID
│   ├── temp/               # 临时目录（博客创建前）
│   │   └── 我的第一篇博客/
│   │       └── 20241201120000_a1b2c3d4.jpg
│   └── 2001/               # 博客ID
│       └── 我的第一篇博客/
│           └── 20241201120000_a1b2c3d4.jpg
└── 1002/                   # 另一个用户ID
    └── 2002/
        └── Spring_Boot教程/
            └── 20241201120100_e5f6g7h8.png
```

### 文件命名规则

- **格式**：`yyyyMMddHHmmss_随机8位字符.扩展名`
- **示例**：`20241201120000_a1b2c3d4.jpg`
- **说明**：时间戳确保时间顺序，随机字符避免冲突

### 路径生成逻辑

1. **创建博客前**：`/userId/temp/cleanTitle/fileName`
2. **创建博客后**：`/userId/blogId/cleanTitle/fileName`
3. **标题清理**：移除特殊字符，保留字母、数字、中文、下划线和短横线

## API 接口

### 1. 上传博客图片

```http
POST /file/upload/blog-image
Authorization: Required
Content-Type: multipart/form-data

Parameters:
- file: 图片文件（必需）
- title: 博客标题（必需）
- blogId: 博客ID（可选，创建博客时为空）
```

**请求示例**：
```javascript
const formData = new FormData();
formData.append('file', imageFile);
formData.append('title', '我的第一篇博客');
// blogId在创建博客时不传，编辑时传入

fetch('/api/file/upload/blog-image', {
    method: 'POST',
    body: formData,
    headers: {
        'Authorization': 'Bearer your-token'
    }
})
.then(response => response.json())
.then(data => {
    console.log('图片URL:', data.data);
});
```

**响应示例**：
```json
{
    "code": 0,
    "message": "success",
    "data": "https://gigalike-1307454348.cos.ap-beijing.myqcloud.com/1001/temp/我的第一篇博客/20241201120000_a1b2c3d4.jpg"
}
```

### 2. 删除文件

```http
DELETE /file/delete?fileUrl=图片URL
Authorization: Required
```

**请求示例**：
```javascript
fetch('/api/file/delete?fileUrl=' + encodeURIComponent(imageUrl), {
    method: 'DELETE',
    headers: {
        'Authorization': 'Bearer your-token'
    }
})
.then(response => response.json())
.then(data => {
    console.log('删除结果:', data.data);
});
```

## 使用流程

### 1. 创建博客时上传图片

```javascript
// 1. 先上传图片（此时blogId为空，使用临时路径）
const uploadImage = async (imageFile, title) => {
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('title', title);
    
    const response = await fetch('/api/file/upload/blog-image', {
        method: 'POST',
        body: formData
    });
    
    const result = await response.json();
    return result.data; // 返回图片URL
};

// 2. 创建博客（包含图片URL）
const createBlog = async (blogData) => {
    const response = await fetch('/api/blog/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            title: blogData.title,
            content: blogData.content,
            coverImg: blogData.imageUrl, // 使用上传的图片URL
            topicNames: blogData.topics
        })
    });
    
    return response.json();
};

// 3. 完整流程
const handleCreateBlog = async () => {
    try {
        // 上传封面图片
        const imageUrl = await uploadImage(selectedImage, blogTitle);
        
        // 创建博客
        const result = await createBlog({
            title: blogTitle,
            content: blogContent,
            imageUrl: imageUrl,
            topics: selectedTopics
        });
        
        console.log('博客创建成功:', result);
        
        // 注意：后端会自动将临时路径的图片迁移到正式路径
        
    } catch (error) {
        console.error('创建失败:', error);
    }
};
```

### 2. 编辑博客时上传图片

```javascript
// 编辑时可以传入blogId，直接使用正式路径
const uploadImageForEdit = async (imageFile, title, blogId) => {
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('title', title);
    formData.append('blogId', blogId); // 传入博客ID
    
    const response = await fetch('/api/file/upload/blog-image', {
        method: 'POST',
        body: formData
    });
    
    const result = await response.json();
    return result.data;
};
```

## 前端集成示例

### React组件示例

```jsx
import React, { useState } from 'react';

const ImageUpload = ({ onImageUploaded, blogTitle, blogId }) => {
    const [uploading, setUploading] = useState(false);
    const [imageUrl, setImageUrl] = useState('');

    const handleFileChange = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // 文件类型验证
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            alert('不支持的图片格式，仅支持：jpg、png、gif、webp');
            return;
        }

        // 文件大小验证
        if (file.size > 5 * 1024 * 1024) {
            alert('图片大小不能超过5MB');
            return;
        }

        setUploading(true);

        try {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('title', blogTitle);
            if (blogId) {
                formData.append('blogId', blogId);
            }

            const response = await fetch('/api/file/upload/blog-image', {
                method: 'POST',
                body: formData
            });

            const result = await response.json();
            
            if (result.code === 0) {
                setImageUrl(result.data);
                onImageUploaded(result.data);
            } else {
                alert('上传失败：' + result.message);
            }
        } catch (error) {
            console.error('上传失败:', error);
            alert('上传失败，请重试');
        } finally {
            setUploading(false);
        }
    };

    return (
        <div className="image-upload">
            <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                disabled={uploading}
            />
            
            {uploading && <div>上传中...</div>}
            
            {imageUrl && (
                <div className="preview">
                    <img src={imageUrl} alt="预览" style={{ maxWidth: '200px' }} />
                    <button onClick={() => {
                        setImageUrl('');
                        onImageUploaded('');
                    }}>
                        删除
                    </button>
                </div>
            )}
        </div>
    );
};

export default ImageUpload;
```

### Vue组件示例

```vue
<template>
  <div class="image-upload">
    <input
      type="file"
      accept="image/*"
      @change="handleFileChange"
      :disabled="uploading"
    />
    
    <div v-if="uploading">上传中...</div>
    
    <div v-if="imageUrl" class="preview">
      <img :src="imageUrl" alt="预览" style="max-width: 200px" />
      <button @click="removeImage">删除</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ImageUpload',
  props: {
    blogTitle: {
      type: String,
      required: true
    },
    blogId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      uploading: false,
      imageUrl: ''
    };
  },
  methods: {
    async handleFileChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      // 文件验证
      const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
      if (!allowedTypes.includes(file.type)) {
        this.$message.error('不支持的图片格式');
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        this.$message.error('图片大小不能超过5MB');
        return;
      }

      this.uploading = true;

      try {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('title', this.blogTitle);
        if (this.blogId) {
          formData.append('blogId', this.blogId);
        }

        const response = await this.$http.post('/file/upload/blog-image', formData);
        
        if (response.data.code === 0) {
          this.imageUrl = response.data.data;
          this.$emit('image-uploaded', this.imageUrl);
        } else {
          this.$message.error('上传失败：' + response.data.message);
        }
      } catch (error) {
        console.error('上传失败:', error);
        this.$message.error('上传失败，请重试');
      } finally {
        this.uploading = false;
      }
    },

    removeImage() {
      this.imageUrl = '';
      this.$emit('image-uploaded', '');
    }
  }
};
</script>
```

## 最佳实践

### 1. 图片优化
- 上传前进行图片压缩
- 使用WebP格式提升加载速度
- 设置合适的图片尺寸

### 2. 用户体验
- 显示上传进度
- 提供图片预览功能
- 支持拖拽上传

### 3. 错误处理
- 文件类型验证
- 文件大小限制
- 网络错误重试机制

### 4. 性能优化
- 使用CDN加速图片访问
- 设置合适的缓存策略
- 图片懒加载

## 安全考虑

### 1. 文件验证
- 严格的文件类型检查
- 文件大小限制
- 文件内容验证

### 2. 访问控制
- 登录用户才能上传
- 用户只能删除自己的文件
- 防止路径遍历攻击

### 3. 存储安全
- 使用HTTPS传输
- 设置合适的CORS策略
- 定期清理无效文件

## 故障排除

### 常见问题

1. **上传失败**
   - 检查COS配置是否正确
   - 验证网络连接
   - 查看服务器日志

2. **图片无法访问**
   - 检查COS桶权限设置
   - 验证域名配置
   - 确认文件是否存在

3. **路径更新失败**
   - 检查博客创建是否成功
   - 验证文件复制权限
   - 查看错误日志

### 日志查看

```bash
# 查看上传相关日志
grep "文件上传" logs/application.log

# 查看错误日志
grep "ERROR" logs/application.log | grep -i "upload\|cos"
```

## 总结

图片上传功能为博客系统提供了完整的图片管理能力，通过智能的路径组织和自动迁移机制，确保了文件的有序存储和高效访问。系统具有良好的扩展性和安全性，可以根据业务需求进行进一步的功能增强。 