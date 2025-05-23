# GigaLike
## 1. 项目简介
### 1.1 项目背景
### 1.2 项目目标
### 1.3 项目功能
## 2. 项目结构
## 3. 项目配置
## 4. 项目运行
### 4.1 环境准备
#### 安装启动 Pulsar

```bash
docker run -it \
-p 6650:6650 \
-p 8080:8080 \
--mount source=pulsardata,target=/pulsar/data \
--mount source=pulsarconf,target=/pulsar/conf \
apachepulsar/pulsar:4.0.3 \
bin/pulsar standalone

```

### 4.2 项目启动
### 4.3 项目访问
## 5. 项目测试
## 6. 项目部署
## 7. 项目维护
## 8. 项目贡献
## 9. 项目许可证
## 10. 项目参考