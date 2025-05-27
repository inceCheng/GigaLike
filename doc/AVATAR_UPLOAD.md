# 用户头像上传功能文档

## 功能概述

实现了用户头像上传功能，用户可以上传头像文件到COS对象存储，然后使用返回的URL更新用户信息。

## 实现的功能

### 1. 头像上传接口

**接口地址：** `POST /api/file/upload/avatar`

**功能：** 上传用户头像到COS对象存储

**请求方式：** `multipart/form-data`

**请求参数：**
- `file`: 头像文件（必需）

**支持格式：** jpg、jpeg、png、gif、webp

**文件限制：** 最大5MB

**响应示例：**
```json
{
  "code": 0,
  "message": "success",
  "data": "https://your-domain.com/avatars/123456/20241201120000_a1b2c3d4.jpg"
}
```

### 2. 更新用户信息接口（已存在）

**接口地址：** `POST /api/user/update`

**功能：** 更新用户信息，包括头像URL

**请求参数：**
```json
{
  "displayName": "用户昵称",
  "avatarUrl": "https://your-domain.com/avatars/123456/20241201120000_a1b2c3d4.jpg",
  "bio": "个人简介",
  "email": "user@example.com"
}
```

## 使用流程

### 完整的头像更新流程

1. **上传头像文件**
   ```bash
   POST /api/file/upload/avatar
   Content-Type: multipart/form-data
   
   file: [头像文件]
   ```

2. **更新用户信息**
   ```bash
   POST /api/user/update
   Content-Type: application/json
   
   {
     "avatarUrl": "上一步返回的头像URL"
   }
   ```

## 技术实现

### 1. 文件存储路径

头像文件存储在COS中的路径格式：
```
avatars/{userId}/{timestamp}_{uuid}.{extension}
```

示例：
```
avatars/123456/20241201120000_a1b2c3d4.jpg
```

### 2. 核心组件

#### 文件上传服务扩展 (`FileUploadService.java`)
- 新增 `uploadUserAvatar` 方法
- 专门处理用户头像上传

#### 文件上传服务实现 (`FileUploadServiceImpl.java`)
- 实现头像上传逻辑
- 文件验证和路径生成
- COS对象存储上传

#### 文件上传控制器扩展 (`FileUploadController.java`)
- 新增 `/upload/avatar` 接口
- 用户身份验证
- 文件上传处理

### 3. 安全特性

#### 文件验证
- **文件类型验证：** 仅支持图片格式
- **文件大小限制：** 最大5MB
- **文件扩展名检查：** 防止恶意文件上传

#### 访问控制
- **登录验证：** 需要用户登录才能上传
- **用户隔离：** 每个用户的头像存储在独立目录

#### 存储安全
- **唯一文件名：** 使用时间戳+UUID防止冲突
- **CDN缓存：** 设置一年缓存时间
- **HTTPS访问：** 确保传输安全

## 前端集成示例

### HTML表单示例

```html
<form id="avatarForm" enctype="multipart/form-data">
    <div class="avatar-upload">
        <img id="avatarPreview" src="current-avatar-url" alt="头像预览">
        <input type="file" id="avatarFile" accept="image/*" style="display: none;">
        <button type="button" onclick="document.getElementById('avatarFile').click()">
            选择头像
        </button>
    </div>
    <button type="submit">保存头像</button>
</form>

<script>
// 头像预览
document.getElementById('avatarFile').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('avatarPreview').src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});

// 上传头像
document.getElementById('avatarForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const fileInput = document.getElementById('avatarFile');
    const file = fileInput.files[0];
    
    if (!file) {
        alert('请选择头像文件');
        return;
    }
    
    // 1. 上传头像文件
    const formData = new FormData();
    formData.append('file', file);
    
    try {
        const uploadResponse = await fetch('/api/file/upload/avatar', {
            method: 'POST',
            body: formData
        });
        
        const uploadResult = await uploadResponse.json();
        
        if (uploadResult.code === 0) {
            // 2. 更新用户信息
            const updateResponse = await fetch('/api/user/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    avatarUrl: uploadResult.data
                })
            });
            
            const updateResult = await updateResponse.json();
            
            if (updateResult.code === 0) {
                alert('头像更新成功');
            } else {
                alert('头像更新失败：' + updateResult.message);
            }
        } else {
            alert('头像上传失败：' + uploadResult.message);
        }
    } catch (error) {
        console.error('头像上传失败', error);
        alert('头像上传失败');
    }
});
</script>
```

### Vue.js示例

```vue
<template>
  <div class="avatar-upload">
    <div class="avatar-preview" @click="selectFile">
      <img :src="avatarUrl || defaultAvatar" alt="头像" />
      <div class="upload-overlay">
        <i class="upload-icon">📷</i>
        <span>点击上传头像</span>
      </div>
    </div>
    
    <input
      ref="fileInput"
      type="file"
      accept="image/*"
      style="display: none"
      @change="handleFileSelect"
    />
    
    <div class="upload-progress" v-if="uploading">
      <div class="progress-bar" :style="{ width: uploadProgress + '%' }"></div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      avatarUrl: '',
      defaultAvatar: '/default-avatar.png',
      uploading: false,
      uploadProgress: 0
    };
  },
  methods: {
    selectFile() {
      this.$refs.fileInput.click();
    },
    
    handleFileSelect(event) {
      const file = event.target.files[0];
      if (file) {
        this.uploadAvatar(file);
      }
    },
    
    async uploadAvatar(file) {
      // 文件验证
      if (!this.validateFile(file)) {
        return;
      }
      
      this.uploading = true;
      this.uploadProgress = 0;
      
      try {
        // 1. 上传头像文件
        const avatarUrl = await this.uploadFile(file);
        
        // 2. 更新用户信息
        await this.updateUserInfo(avatarUrl);
        
        this.avatarUrl = avatarUrl;
        this.$message.success('头像更新成功');
        
      } catch (error) {
        console.error('头像上传失败', error);
        this.$message.error('头像上传失败');
      } finally {
        this.uploading = false;
        this.uploadProgress = 0;
      }
    },
    
    validateFile(file) {
      // 文件类型验证
      const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
      if (!allowedTypes.includes(file.type)) {
        this.$message.error('不支持的文件格式，请选择jpg、png、gif或webp格式的图片');
        return false;
      }
      
      // 文件大小验证
      const maxSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxSize) {
        this.$message.error('文件大小不能超过5MB');
        return false;
      }
      
      return true;
    },
    
    async uploadFile(file) {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await this.$http.post('/api/file/upload/avatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: (progressEvent) => {
          this.uploadProgress = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
        }
      });
      
      if (response.data.code === 0) {
        return response.data.data;
      } else {
        throw new Error(response.data.message);
      }
    },
    
    async updateUserInfo(avatarUrl) {
      const response = await this.$http.post('/api/user/update', {
        avatarUrl: avatarUrl
      });
      
      if (response.data.code !== 0) {
        throw new Error(response.data.message);
      }
    }
  }
};
</script>

<style scoped>
.avatar-upload {
  display: inline-block;
  position: relative;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  position: relative;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-preview:hover .upload-overlay {
  opacity: 1;
}

.upload-progress {
  width: 100px;
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  margin-top: 8px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: #1890ff;
  transition: width 0.3s;
}
</style>
```

### React示例

```jsx
import React, { useState, useRef } from 'react';

const AvatarUpload = ({ currentAvatar, onAvatarUpdate }) => {
  const [avatarUrl, setAvatarUrl] = useState(currentAvatar);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);

  const handleFileSelect = (event) => {
    const file = event.target.files[0];
    if (file) {
      uploadAvatar(file);
    }
  };

  const validateFile = (file) => {
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    if (!allowedTypes.includes(file.type)) {
      alert('不支持的文件格式');
      return false;
    }

    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
      alert('文件大小不能超过5MB');
      return false;
    }

    return true;
  };

  const uploadAvatar = async (file) => {
    if (!validateFile(file)) {
      return;
    }

    setUploading(true);

    try {
      // 1. 上传头像文件
      const formData = new FormData();
      formData.append('file', file);

      const uploadResponse = await fetch('/api/file/upload/avatar', {
        method: 'POST',
        body: formData
      });

      const uploadResult = await uploadResponse.json();

      if (uploadResult.code === 0) {
        // 2. 更新用户信息
        const updateResponse = await fetch('/api/user/update', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            avatarUrl: uploadResult.data
          })
        });

        const updateResult = await updateResponse.json();

        if (updateResult.code === 0) {
          setAvatarUrl(uploadResult.data);
          onAvatarUpdate && onAvatarUpdate(uploadResult.data);
          alert('头像更新成功');
        } else {
          throw new Error(updateResult.message);
        }
      } else {
        throw new Error(uploadResult.message);
      }
    } catch (error) {
      console.error('头像上传失败', error);
      alert('头像上传失败：' + error.message);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="avatar-upload">
      <div 
        className="avatar-preview"
        onClick={() => fileInputRef.current?.click()}
      >
        <img 
          src={avatarUrl || '/default-avatar.png'} 
          alt="头像" 
        />
        <div className="upload-overlay">
          <span>📷</span>
          <span>点击上传</span>
        </div>
      </div>
      
      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        style={{ display: 'none' }}
        onChange={handleFileSelect}
      />
      
      {uploading && (
        <div className="upload-status">
          上传中...
        </div>
      )}
    </div>
  );
};

export default AvatarUpload;
```

## 错误处理

### 常见错误码

- `头像文件不能为空`: 未选择文件
- `不支持的图片格式`: 文件格式不正确
- `头像文件大小不能超过5MB`: 文件过大
- `头像上传失败`: COS上传失败
- `用户ID不能为空`: 用户未登录

### 容错机制

- **文件验证：** 严格验证文件类型和大小
- **错误日志：** 记录上传失败的详细信息
- **用户反馈：** 提供清晰的错误提示
- **重试机制：** 前端可实现上传重试

## 部署注意事项

1. **COS配置**：确保COS服务配置正确
2. **域名设置**：配置CDN加速域名
3. **权限设置**：确保COS桶有正确的读写权限
4. **CORS配置**：如果前端跨域，需要配置CORS
5. **文件清理**：可以定期清理未使用的头像文件

## 性能优化

1. **图片压缩**：前端可以在上传前压缩图片
2. **缓存策略**：设置合适的CDN缓存时间
3. **懒加载**：头像图片使用懒加载
4. **WebP格式**：支持WebP格式减少文件大小
5. **进度显示**：显示上传进度提升用户体验

## 后续优化建议

1. **图片裁剪**：集成图片裁剪功能
2. **多尺寸生成**：自动生成不同尺寸的头像
3. **默认头像**：提供多种默认头像选择
4. **头像历史**：保存用户的头像历史记录
5. **批量上传**：支持批量上传多个头像

用户头像上传功能已完全实现并可投入使用！ 