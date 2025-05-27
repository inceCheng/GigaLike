# 邮箱验证码功能更新说明

## 更新内容

### 主要变更
- **移除发送邮箱验证码时的图形验证码依赖**
- **简化邮箱验证码获取流程**
- **保持注册时的图形验证码验证**

### 更新前后对比

#### 更新前流程
1. 获取图形验证码
2. 输入邮箱和图形验证码，发送邮箱验证码
3. 获取新的图形验证码
4. 输入所有信息进行注册

#### 更新后流程
1. 直接输入邮箱，发送邮箱验证码
2. 获取图形验证码
3. 输入所有信息进行注册

## 接口变更

### 发送邮箱验证码接口

**接口地址：** `POST /api/email/send-code`

**更新前请求参数：**
```json
{
  "email": "user@example.com",
  "captchaCode": "A1B2",
  "captchaId": "图形验证码ID"
}
```

**更新后请求参数：**
```json
{
  "email": "user@example.com"
}
```

### 注册接口（无变更）

**接口地址：** `POST /api/user/register`

**请求参数：**（保持不变）
```json
{
  "username": "newuser",
  "password": "password123",
  "confirmPassword": "password123",
  "email": "user@example.com",
  "emailCode": "123456",
  "captchaCode": "A1B2",
  "captchaId": "图形验证码ID"
}
```

## 安全性说明

### 保持的安全措施
- **频率限制：** 同一邮箱60秒内只能发送一次验证码
- **验证码过期：** 邮箱验证码10分钟后自动过期
- **一次性使用：** 验证成功后立即删除验证码
- **注册验证：** 注册时仍需验证图形验证码

### 简化的原因
- **用户体验优化：** 减少用户操作步骤
- **流程简化：** 邮箱验证码主要用于验证邮箱所有权
- **安全平衡：** 在注册最终步骤保留图形验证码验证

## 前端适配

### JavaScript示例更新

```javascript
// 发送邮箱验证码（简化版）
async function sendEmailCode() {
  const emailData = {
    email: document.getElementById('email').value
  };
  
  const response = await fetch('/api/email/send-code', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(emailData)
  });
  
  const result = await response.json();
  
  if (result.code === 0) {
    alert('邮箱验证码发送成功');
    startCountdown();
  } else {
    alert(result.message);
  }
}
```

### Vue.js示例更新

```javascript
async sendEmailCode() {
  if (!this.email) {
    this.$message.error('请填写邮箱');
    return;
  }
  
  try {
    const response = await this.$http.post('/api/email/send-code', {
      email: this.email
    });
    
    if (response.data.code === 0) {
      this.$message.success('邮箱验证码发送成功');
      this.startCountdown();
    } else {
      this.$message.error(response.data.message);
    }
  } catch (error) {
    console.error('发送邮箱验证码失败', error);
  }
}
```

## 测试更新

### 新的测试流程

```bash
# 1. 发送邮箱验证码（无需图形验证码）
curl -X POST http://localhost:8123/api/email/send-code \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com"
  }'

# 2. 获取图形验证码
curl -X GET http://localhost:8123/api/captcha/generate

# 3. 用户注册
curl -X POST http://localhost:8123/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "confirmPassword": "password123",
    "email": "test@example.com",
    "emailCode": "从邮箱中收到的6位验证码",
    "captchaCode": "图形验证码",
    "captchaId": "图形验证码ID"
  }'
```

## 兼容性说明

- **向后兼容：** 注册接口保持不变
- **前端更新：** 需要更新前端发送邮箱验证码的逻辑
- **API文档：** 已更新相关接口文档

## 优势

1. **用户体验提升：** 减少操作步骤，提高转化率
2. **流程简化：** 降低用户理解成本
3. **安全平衡：** 在关键步骤保持安全验证
4. **开发效率：** 简化前端逻辑

邮箱验证码功能已成功更新，提供更好的用户体验！ 