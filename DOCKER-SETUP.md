# GigaLike Docker 环境配置指南

## 🔧 环境变量配置

### 1. 创建环境变量文件

```bash
# 复制示例文件
cp env.example .env

# 编辑 .env 文件，填入真实的配置值
vi .env  # 或使用你喜欢的编辑器
```

### 2. 配置说明

在 `.env` 文件中配置以下变量：

```properties
# 腾讯云COS配置（必填）
COS_SECRET_ID=你的腾讯云SecretId
COS_SECRET_KEY=你的腾讯云SecretKey

# 数据库配置（可选，使用默认值）
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=gigalikedb
MYSQL_USERNAME=root
MYSQL_PASSWORD=123456

# Redis配置（可选，使用默认值）
REDIS_HOST=localhost
REDIS_PORT=6379

# 邮箱配置（如果使用邮件功能）
MAIL_USERNAME=你的邮箱@qq.com
MAIL_PASSWORD=你的邮箱授权码
```

## 🚀 启动步骤

### 方法一：使用脚本启动

```bash
# Windows
docker-start.bat start

# Linux/Mac
chmod +x docker-start.sh
./docker-start.sh start
```

### 方法二：手动启动

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 🎯 开发模式

### 混合开发（推荐）

只启动中间件，应用在 IDEA 中运行：

```bash
# 只启动中间件
docker-compose up -d mysql redis pulsar

# 在 IDEA 中配置环境变量
# Run Configuration -> Environment Variables:
# COS_SECRET_ID=你的SecretId
# COS_SECRET_KEY=你的SecretKey
```

### 完全容器化

所有服务都在容器中运行：

```bash
# 启动所有服务
./docker-start.sh start

# 访问应用
curl http://localhost:8080
```

## 🔒 安全注意事项

1. **永远不要提交 `.env` 文件到 Git**
2. **定期更换密钥**
3. **为不同环境使用不同的密钥**
4. **限制密钥的权限范围**

## 🛠 故障排除

### 应用无法连接到中间件

```bash
# 检查网络连接
docker network ls
docker network inspect gigalike_gigalike-network

# 检查服务状态
docker-compose ps
```

### 查看详细日志

```bash
# 查看特定服务日志
docker-compose logs gigalike-app
docker-compose logs mysql
docker-compose logs redis
docker-compose logs pulsar
```

### 重置环境

```bash
# 停止并删除所有容器
docker-compose down

# 删除数据卷（谨慎操作）
docker-compose down -v

# 重新启动
docker-compose up -d
``` 