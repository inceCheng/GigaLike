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

#### Java 17+ 环境配置

由于项目使用Java 21和Apache Pulsar，需要添加JVM参数解决模块访问权限问题。

**IDE运行配置（IntelliJ IDEA）：**
1. 打开 `Run/Debug Configurations`
2. 选择Spring Boot应用配置
3. 在 `VM options` 中添加：
```
--add-opens java.base/sun.net=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED
```

**Maven运行：**
```bash
mvn spring-boot:run
```
（JVM参数已在pom.xml中配置）

**命令行运行：**
```bash
java --add-opens java.base/sun.net=ALL-UNNAMED \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.net=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     -jar target/GigaLike-0.0.1-SNAPSHOT.jar
```

详细说明请参考：[doc/PULSAR_JAVA_MODULE_FIX.md](doc/PULSAR_JAVA_MODULE_FIX.md)

### 4.2 项目启动
### 4.3 项目访问
## 5. 项目测试
## 6. 项目部署
## 7. 项目维护
## 8. 项目贡献
## 9. 项目许可证
## 10. 项目参考