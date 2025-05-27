# 邮箱验证码功能实现文档

## 功能概述

在用户注册流程中添加了邮箱验证码功能，用户需要先获取图形验证码，然后输入邮箱获取邮箱验证码，最后提交完整的注册信息进行注册。

## 实现的功能

### 1. 发送邮箱验证码接口

**接口地址：** `POST /api/email/send-code`

**功能：** 发送邮箱验证码，无需图形验证码

**请求参数：**
```json
{
  "email": "user@example.com"
}
```

**响应示例：**
```json
{
  "code": 0,
  "message": "success",
  "data": "邮箱验证码发送成功"
}
```

### 2. 注册接口更新

**接口地址：** `POST /api/user/register`

**新增参数：**
- `email`: 用户邮箱
- `emailCode`: 邮箱验证码

**完整请求参数：**
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

## 注册流程

### 完整的注册流程

1. **发送邮箱验证码**
   ```bash
   POST /api/email/send-code
   {
     "email": "user@example.com"
   }
   ```

2. **获取图形验证码**
   ```bash
   GET /api/captcha/generate
   ```

3. **用户注册**
   ```bash
   POST /api/user/register
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

## 技术实现

### 1. 依赖添加

在 `pom.xml` 中添加了Spring Boot邮件发送依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

### 2. 核心组件

#### 邮件配置类 (`EmailConfig.java`)
- 配置SMTP服务器信息
- 支持QQ邮箱、163邮箱等主流邮箱服务商
- 启用STARTTLS加密传输

#### 邮件服务 (`EmailService.java` & `EmailServiceImpl.java`)
- **发送邮箱验证码：** 直接发送6位数字验证码，无需图形验证码
- **验证邮箱验证码：** 验证用户输入的邮箱验证码
- **频率限制：** 同一邮箱60秒内只能发送一次验证码
- **存储管理：** 使用Redis存储验证码，10分钟过期

#### 邮箱验证码控制器 (`EmailController.java`)
- 提供发送邮箱验证码的RESTful API
- 集成Swagger文档

#### DTO更新
- `EmailCodeRequest.java`: 邮箱验证码请求对象
- `UserRegisterRequest.java`: 添加邮箱和邮箱验证码字段

### 3. 安全特性

#### 防刷机制
- **频率限制：** 同一邮箱60秒内只能发送一次验证码
- **验证码过期：** 邮箱验证码10分钟后自动过期
- **一次性使用：** 验证成功后立即删除验证码
- **图形验证码验证：** 注册时需要验证图形验证码

#### 数据验证
- **邮箱格式验证：** 使用@Email注解验证邮箱格式
- **邮箱唯一性：** 注册时检查邮箱是否已被使用
- **参数完整性：** 验证所有必需参数是否提供

## 配置说明

### 邮件服务器配置

在 `application-local.yml` 中配置邮件服务器信息：

```yaml
spring:
  mail:
    host: smtp.qq.com  # 邮件服务器地址
    port: 587  # 端口号
    username: your_email@qq.com  # 发送方邮箱
    password: your_email_auth_code  # 邮箱授权码
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### 常用邮箱服务器配置

#### QQ邮箱
```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your_email@qq.com
    password: your_auth_code  # QQ邮箱授权码
```

#### 163邮箱
```yaml
spring:
  mail:
    host: smtp.163.com
    port: 25
    username: your_email@163.com
    password: your_auth_code  # 163邮箱授权码
```

#### Gmail
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password  # Gmail应用专用密码
```

### 获取邮箱授权码

#### QQ邮箱
1. 登录QQ邮箱网页版
2. 点击"设置" -> "账户"
3. 找到"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"
4. 开启"POP3/SMTP服务"
5. 生成授权码

#### 163邮箱
1. 登录163邮箱网页版
2. 点击"设置" -> "POP3/SMTP/IMAP"
3. 开启"POP3/SMTP服务"
4. 设置客户端授权密码

## 前端集成示例

### JavaScript示例

```javascript
// 1. 获取图形验证码
async function getCaptcha() {
  const response = await fetch('/api/captcha/generate');
  const result = await response.json();
  
  if (result.code === 0) {
    document.getElementById('captcha-img').src = result.data.image;
    document.getElementById('captcha-id').value = result.data.id;
  }
}

// 2. 发送邮箱验证码
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
    startCountdown(); // 开始倒计时
  } else {
    alert(result.message);
    getCaptcha(); // 刷新图形验证码
  }
}

// 3. 用户注册
async function register() {
  const registerData = {
    username: document.getElementById('username').value,
    password: document.getElementById('password').value,
    confirmPassword: document.getElementById('confirm-password').value,
    email: document.getElementById('email').value,
    emailCode: document.getElementById('email-code').value,
    captchaCode: document.getElementById('captcha-code').value,
    captchaId: document.getElementById('captcha-id').value
  };
  
  const response = await fetch('/api/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(registerData)
  });
  
  const result = await response.json();
  
  if (result.code === 0) {
    alert('注册成功');
    // 跳转到登录页面
  } else {
    alert(result.message);
    getCaptcha(); // 刷新图形验证码
  }
}

// 发送验证码倒计时
function startCountdown() {
  const button = document.getElementById('send-email-code-btn');
  let countdown = 60;
  
  button.disabled = true;
  button.textContent = `${countdown}秒后重新发送`;
  
  const timer = setInterval(() => {
    countdown--;
    if (countdown > 0) {
      button.textContent = `${countdown}秒后重新发送`;
    } else {
      button.disabled = false;
      button.textContent = '发送验证码';
      clearInterval(timer);
    }
  }, 1000);
}
```

### Vue.js示例

```vue
<template>
  <div class="register-form">
    <input v-model="username" placeholder="用户名" />
    <input v-model="password" type="password" placeholder="密码" />
    <input v-model="confirmPassword" type="password" placeholder="确认密码" />
    <input v-model="email" type="email" placeholder="邮箱" />
    
    <div class="captcha-section">
      <img :src="captchaImage" @click="refreshCaptcha" />
      <input v-model="captchaCode" placeholder="图形验证码" />
    </div>
    
    <div class="email-code-section">
      <input v-model="emailCode" placeholder="邮箱验证码" />
      <button 
        @click="sendEmailCode" 
        :disabled="countdown > 0"
        class="send-code-btn"
      >
        {{ countdown > 0 ? `${countdown}秒后重新发送` : '发送验证码' }}
      </button>
    </div>
    
    <button @click="register" class="register-btn">注册</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      emailCode: '',
      captchaCode: '',
      captchaId: '',
      captchaImage: '',
      countdown: 0
    };
  },
  mounted() {
    this.getCaptcha();
  },
  methods: {
    async getCaptcha() {
      try {
        const response = await this.$http.get('/api/captcha/generate');
        if (response.data.code === 0) {
          this.captchaImage = response.data.data.image;
          this.captchaId = response.data.data.id;
        }
      } catch (error) {
        console.error('获取验证码失败', error);
      }
    },
    
    refreshCaptcha() {
      this.getCaptcha();
      this.captchaCode = '';
    },
    
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
          this.refreshCaptcha();
        }
      } catch (error) {
        console.error('发送邮箱验证码失败', error);
        this.refreshCaptcha();
      }
    },
    
    async register() {
      try {
        const response = await this.$http.post('/api/user/register', {
          username: this.username,
          password: this.password,
          confirmPassword: this.confirmPassword,
          email: this.email,
          emailCode: this.emailCode,
          captchaCode: this.captchaCode,
          captchaId: this.captchaId
        });
        
        if (response.data.code === 0) {
          this.$message.success('注册成功');
          this.$router.push('/login');
        } else {
          this.$message.error(response.data.message);
          this.refreshCaptcha();
        }
      } catch (error) {
        console.error('注册失败', error);
        this.refreshCaptcha();
      }
    },
    
    startCountdown() {
      this.countdown = 60;
      const timer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          clearInterval(timer);
        }
      }, 1000);
    }
  }
};
</script>
```

## 错误处理

### 常见错误码

- `参数为空`: 必需参数未提供
- `邮箱格式不正确`: 邮箱格式验证失败
- `图形验证码错误`: 图形验证码验证失败
- `邮箱验证码错误`: 邮箱验证码验证失败
- `该邮箱已被注册`: 邮箱已被其他用户使用
- `邮箱验证码发送失败`: 邮件发送失败

### 容错机制

- **邮件发送失败：** 记录错误日志，返回失败响应
- **Redis连接失败：** 验证码功能不可用
- **频率限制：** 防止恶意刷取验证码
- **参数验证：** 严格验证所有输入参数

## 部署注意事项

1. **邮件服务器配置**：确保邮件服务器信息正确
2. **授权码获取**：使用邮箱授权码而非登录密码
3. **网络连接**：确保服务器能访问邮件服务器
4. **Redis服务**：确保Redis服务正常运行
5. **防火墙设置**：开放邮件服务器端口

## 监控和日志

### 关键日志记录

- 邮箱验证码发送成功/失败
- 邮箱验证码验证结果
- 频率限制触发情况
- 邮件发送异常

### 监控指标

- 邮箱验证码发送成功率
- 邮箱验证码验证成功率
- 邮件发送延迟
- Redis连接状态

## 后续优化建议

1. **邮件模板美化**：使用HTML邮件模板
2. **多语言支持**：支持不同语言的邮件内容
3. **邮件队列**：使用消息队列异步发送邮件
4. **发送统计**：记录邮件发送统计信息
5. **邮件追踪**：追踪邮件送达状态

邮箱验证码功能已完全实现并可投入使用！ 