# 验证码功能快速启动指南

## 前置条件

1. **Redis服务运行**
   ```bash
   # 启动Redis服务
   redis-server
   
   # 或使用Docker
   docker run -d -p 6379:6379 redis:latest
   ```

2. **项目依赖安装**
   ```bash
   mvn clean install
   ```

## 快速测试

### 1. 启动项目
```bash
mvn spring-boot:run
```

### 2. 测试验证码生成
```bash
# 生成验证码
curl -X GET http://localhost:8123/api/captcha/generate

# 响应示例
{
  "code": 0,
  "message": "success",
  "data": {
    "id": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
    "image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAH..."
  }
}
```

### 3. 测试登录接口
```bash
# 使用验证码登录（需要先注册用户）
curl -X POST http://localhost:8123/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "captchaCode": "从图片中看到的验证码",
    "captchaId": "上一步获取的验证码ID"
  }'
```

### 4. 测试注册接口
```bash
# 使用验证码注册
curl -X POST http://localhost:8123/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "confirmPassword": "password123",
    "captchaCode": "从图片中看到的验证码",
    "captchaId": "验证码ID"
  }'
```

## 前端集成示例

### JavaScript示例
```javascript
// 1. 获取验证码
async function getCaptcha() {
  const response = await fetch('/api/captcha/generate');
  const result = await response.json();
  
  if (result.code === 0) {
    // 显示验证码图片
    document.getElementById('captcha-img').src = result.data.image;
    // 保存验证码ID
    document.getElementById('captcha-id').value = result.data.id;
  }
}

// 2. 登录时包含验证码
async function login() {
  const loginData = {
    username: document.getElementById('username').value,
    password: document.getElementById('password').value,
    captchaCode: document.getElementById('captcha-code').value,
    captchaId: document.getElementById('captcha-id').value
  };
  
  const response = await fetch('/api/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(loginData)
  });
  
  const result = await response.json();
  
  if (result.code === 0) {
    // 登录成功
    console.log('登录成功', result.data);
  } else {
    // 登录失败，刷新验证码
    getCaptcha();
    alert(result.message);
  }
}

// 页面加载时获取验证码
window.onload = function() {
  getCaptcha();
};
```

### Vue.js示例
```vue
<template>
  <div>
    <input v-model="username" placeholder="用户名" />
    <input v-model="password" type="password" placeholder="密码" />
    <div>
      <img :src="captchaImage" @click="refreshCaptcha" style="cursor: pointer" />
      <input v-model="captchaCode" placeholder="验证码" />
    </div>
    <button @click="login">登录</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      captchaCode: '',
      captchaId: '',
      captchaImage: ''
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
    
    async login() {
      try {
        const response = await this.$http.post('/api/user/login', {
          username: this.username,
          password: this.password,
          captchaCode: this.captchaCode,
          captchaId: this.captchaId
        });
        
        if (response.data.code === 0) {
          this.$message.success('登录成功');
          // 处理登录成功逻辑
        } else {
          this.$message.error(response.data.message);
          this.refreshCaptcha();
        }
      } catch (error) {
        console.error('登录失败', error);
        this.refreshCaptcha();
      }
    }
  }
};
</script>
```

## 常见问题

### 1. Redis连接失败
**错误：** `Unable to connect to Redis`
**解决：** 检查Redis服务是否启动，配置是否正确

### 2. 验证码图片不显示
**错误：** 前端无法显示验证码图片
**解决：** 检查Base64格式是否正确，浏览器是否支持

### 3. 验证码总是错误
**错误：** 输入正确验证码仍提示错误
**解决：** 检查大小写，确认验证码未过期

### 4. 字体问题
**错误：** 验证码图片显示异常
**解决：** 确保服务器安装了中文字体

## 配置调优

### 验证码参数调整
在 `CaptchaConfig.java` 中可以调整：

```java
// 验证码长度
properties.setProperty("kaptcha.textproducer.char.length", "4");

// 图片尺寸
properties.setProperty("kaptcha.image.width", "120");
properties.setProperty("kaptcha.image.height", "40");

// 字符范围（只使用数字）
properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
```

### Redis过期时间调整
在 `CaptchaServiceImpl.java` 中：

```java
// 修改过期时间为10分钟
private static final long CAPTCHA_EXPIRE_MINUTES = 10;
```

## 监控和日志

### 添加日志记录
```java
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {
    
    @Override
    public CaptchaResponse generateCaptcha() {
        log.info("生成验证码请求");
        // ... 现有代码
        log.info("验证码生成成功，ID: {}", captchaId);
        return new CaptchaResponse(captchaId, "data:image/png;base64," + base64Image);
    }
    
    @Override
    public boolean verifyCaptcha(String captchaId, String captchaCode) {
        log.info("验证验证码，ID: {}, Code: {}", captchaId, captchaCode);
        // ... 现有代码
        log.info("验证码验证结果: {}", result);
        return result;
    }
}
```

验证码功能现已完全实现并可投入使用！ 