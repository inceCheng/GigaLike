# 邮箱验证码功能快速启动指南

## 前置条件

### 1. 基础环境
- Redis服务正常运行
- 项目依赖已安装

### 2. 邮箱服务配置

#### 获取QQ邮箱授权码（推荐）
1. 登录 [QQ邮箱网页版](https://mail.qq.com)
2. 点击"设置" -> "账户"
3. 找到"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"
4. 开启"POP3/SMTP服务"
5. 点击"生成授权码"，按提示操作
6. 保存生成的授权码（16位字符）

#### 配置邮件服务
在 `src/main/resources/application-local.yml` 中添加：

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your_email@qq.com  # 替换为你的QQ邮箱
    password: your_auth_code     # 替换为你的授权码
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## 快速测试

### 1. 启动项目
```bash
mvn spring-boot:run
```

### 2. 测试完整注册流程

#### 步骤1：发送邮箱验证码
```bash
curl -X POST http://localhost:8123/api/email/send-code \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com"
  }'
```

#### 步骤2：获取图形验证码
```bash
curl -X GET http://localhost:8123/api/captcha/generate
```

#### 步骤3：用户注册
```bash
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

邮箱验证码功能已完全实现，可以投入使用！ 