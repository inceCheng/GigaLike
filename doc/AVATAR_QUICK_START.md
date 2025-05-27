# 用户头像上传功能快速启动指南

## 前置条件

1. **COS对象存储配置**
   - 确保COS服务已正确配置
   - 检查存储桶权限设置

2. **用户登录状态**
   - 用户必须已登录才能上传头像

## 快速测试

### 1. 启动项目
```bash
mvn spring-boot:run
```

### 2. 测试头像上传流程

#### 步骤1：用户登录
```bash
curl -X POST http://localhost:8123/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "captchaCode": "A1B2",
    "captchaId": "captcha-id"
  }'
```

#### 步骤2：上传头像文件
```bash
curl -X POST http://localhost:8123/api/file/upload/avatar \
  -H "Cookie: JSESSIONID=your-session-id" \
  -F "file=@/path/to/avatar.jpg"
```

响应示例：
```json
{
  "code": 0,
  "message": "success",
  "data": "https://your-domain.com/avatars/123456/20241201120000_a1b2c3d4.jpg"
}
```

#### 步骤3：更新用户信息
```bash
curl -X POST http://localhost:8123/api/user/update \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=your-session-id" \
  -d '{
    "avatarUrl": "https://your-domain.com/avatars/123456/20241201120000_a1b2c3d4.jpg"
  }'
```

## 前端集成示例

### 简单的HTML表单

```html
<!DOCTYPE html>
<html>
<head>
    <title>头像上传测试</title>
    <style>
        .avatar-container {
            text-align: center;
            margin: 20px;
        }
        .avatar-preview {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid #ddd;
            margin-bottom: 20px;
        }
        .upload-btn {
            background: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .upload-btn:hover {
            background: #0056b3;
        }
    </style>
</head>
<body>
    <div class="avatar-container">
        <h2>头像上传测试</h2>
        
        <img id="avatarPreview" 
             src="https://via.placeholder.com/150" 
             alt="头像预览" 
             class="avatar-preview">
        
        <br>
        
        <input type="file" 
               id="avatarFile" 
               accept="image/*" 
               style="display: none;">
        
        <button class="upload-btn" onclick="selectFile()">
            选择头像
        </button>
        
        <button class="upload-btn" onclick="uploadAvatar()">
            上传头像
        </button>
        
        <div id="status" style="margin-top: 20px;"></div>
    </div>

    <script>
        let selectedFile = null;

        function selectFile() {
            document.getElementById('avatarFile').click();
        }

        document.getElementById('avatarFile').addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                selectedFile = file;
                
                // 预览图片
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('avatarPreview').src = e.target.result;
                };
                reader.readAsDataURL(file);
                
                updateStatus('已选择文件: ' + file.name);
            }
        });

        async function uploadAvatar() {
            if (!selectedFile) {
                updateStatus('请先选择头像文件', 'error');
                return;
            }

            updateStatus('正在上传...', 'info');

            try {
                // 1. 上传头像文件
                const formData = new FormData();
                formData.append('file', selectedFile);

                const uploadResponse = await fetch('/api/file/upload/avatar', {
                    method: 'POST',
                    body: formData
                });

                const uploadResult = await uploadResponse.json();

                if (uploadResult.code === 0) {
                    updateStatus('头像上传成功', 'success');
                    
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
                        updateStatus('头像更新成功！', 'success');
                    } else {
                        updateStatus('头像更新失败: ' + updateResult.message, 'error');
                    }
                } else {
                    updateStatus('头像上传失败: ' + uploadResult.message, 'error');
                }
            } catch (error) {
                console.error('上传失败', error);
                updateStatus('上传失败: ' + error.message, 'error');
            }
        }

        function updateStatus(message, type = 'info') {
            const statusDiv = document.getElementById('status');
            statusDiv.textContent = message;
            
            // 设置颜色
            switch (type) {
                case 'success':
                    statusDiv.style.color = 'green';
                    break;
                case 'error':
                    statusDiv.style.color = 'red';
                    break;
                default:
                    statusDiv.style.color = 'blue';
            }
        }
    </script>
</body>
</html>
```

## 接口测试

### 使用Postman测试

1. **设置环境变量**
   - `baseUrl`: `http://localhost:8123`

2. **登录获取Session**
   ```
   POST {{baseUrl}}/api/user/login
   Content-Type: application/json
   
   {
     "username": "testuser",
     "password": "password123",
     "captchaCode": "A1B2",
     "captchaId": "captcha-id"
   }
   ```

3. **上传头像**
   ```
   POST {{baseUrl}}/api/file/upload/avatar
   Content-Type: multipart/form-data
   
   file: [选择图片文件]
   ```

4. **更新用户信息**
   ```
   POST {{baseUrl}}/api/user/update
   Content-Type: application/json
   
   {
     "avatarUrl": "上一步返回的URL"
   }
   ```

## 常见问题排查

### 1. 文件上传失败

#### 问题：收到"头像文件不能为空"
**解决方案：**
- 检查请求参数名是否为`file`
- 确认文件确实被选中

#### 问题：收到"不支持的图片格式"
**解决方案：**
- 确保文件格式为jpg、png、gif、webp
- 检查文件的MIME类型

#### 问题：收到"头像文件大小不能超过5MB"
**解决方案：**
- 压缩图片文件
- 选择更小的图片

### 2. 权限问题

#### 问题：收到"未登录"错误
**解决方案：**
- 确保用户已登录
- 检查Session是否有效

#### 问题：COS上传失败
**解决方案：**
- 检查COS配置
- 确认存储桶权限
- 查看服务器日志

### 3. 路径问题

#### 问题：生成的URL无法访问
**解决方案：**
- 检查COS域名配置
- 确认CDN设置
- 验证文件是否真正上传成功

## 监控和日志

### 查看上传日志
```bash
# 查看头像上传相关日志
tail -f logs/application.log | grep -E "(头像|avatar)"

# 查看文件上传日志
tail -f logs/application.log | grep -E "(文件上传|FileUpload)"
```

### 检查COS存储
```bash
# 使用COS CLI工具查看文件
coscli ls cos://your-bucket/avatars/

# 查看特定用户的头像
coscli ls cos://your-bucket/avatars/123456/
```

## 性能测试

### 并发上传测试
```bash
# 使用Apache Bench进行并发测试
ab -n 100 -c 10 -T 'multipart/form-data' \
   -p avatar.jpg \
   http://localhost:8123/api/file/upload/avatar
```

### 文件大小测试
```bash
# 测试不同大小的文件
for size in 1MB 3MB 5MB 6MB; do
  echo "Testing ${size} file..."
  # 创建测试文件并上传
done
```

## 部署清单

### 1. 配置检查
- [ ] COS服务配置正确
- [ ] 存储桶权限设置
- [ ] CDN域名配置
- [ ] CORS设置（如需要）

### 2. 功能测试
- [ ] 头像上传成功
- [ ] 文件格式验证
- [ ] 文件大小限制
- [ ] 用户信息更新

### 3. 安全检查
- [ ] 登录验证生效
- [ ] 文件类型验证
- [ ] 文件大小限制
- [ ] 路径安全性

### 4. 性能测试
- [ ] 上传速度测试
- [ ] 并发处理测试
- [ ] 内存使用监控
- [ ] CDN缓存验证

用户头像上传功能已完全实现，可以投入使用！ 