# Pulsar Java 模块访问权限问题解决方案 🔧

## 问题描述

在Java 17+环境下运行Apache Pulsar时，会遇到以下错误：

```
java.lang.IllegalAccessException: class org.apache.pulsar.common.util.netty.DnsResolverUtil 
cannot access class sun.net.InetAddressCachePolicy (in module java.base) 
because module java.base does not export sun.net to unnamed module
```

这是因为从Java 9开始，Java引入了模块系统（JPMS），限制了对内部API的访问。Pulsar客户端试图访问`sun.net.InetAddressCachePolicy`这个内部类，但被模块系统阻止了。

## 解决方案

### 方案1：添加JVM启动参数（推荐）

#### 1.1 IDE运行配置

**IntelliJ IDEA:**
1. 打开 `Run/Debug Configurations`
2. 选择你的Spring Boot应用配置
3. 在 `VM options` 中添加：

```bash
--add-opens java.base/sun.net=ALL-UNNAMED
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.net=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
```

**Eclipse:**
1. 右键项目 → `Run As` → `Run Configurations`
2. 选择你的Java Application
3. 在 `Arguments` 标签页的 `VM arguments` 中添加上述参数

**VS Code:**
在 `launch.json` 中添加：

```json
{
    "type": "java",
    "name": "Launch GigaLikeApplication",
    "request": "launch",
    "mainClass": "com.ince.gigalike.GigaLikeApplication",
    "vmArgs": [
        "--add-opens", "java.base/sun.net=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.net=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED"
    ]
}
```

#### 1.2 Maven运行配置

在 `pom.xml` 中配置Spring Boot Maven插件：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <jvmArguments>
            --add-opens java.base/sun.net=ALL-UNNAMED
            --add-opens java.base/java.lang=ALL-UNNAMED
            --add-opens java.base/java.net=ALL-UNNAMED
            --add-opens java.base/java.util=ALL-UNNAMED
        </jvmArguments>
        <excludes>
            <exclude>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </exclude>
        </excludes>
    </configuration>
</plugin>
```

#### 1.3 命令行运行

```bash
java --add-opens java.base/sun.net=ALL-UNNAMED \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.net=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     -jar your-application.jar
```

#### 1.4 Docker运行

```dockerfile
FROM openjdk:21-jdk-slim

# 添加JVM参数
ENV JAVA_OPTS="--add-opens java.base/sun.net=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

COPY target/your-application.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
```

### 方案2：升级Pulsar版本

检查是否有更新的Pulsar版本支持Java 17+：

```xml
<!-- 在pom.xml中检查Spring Boot版本对应的Pulsar版本 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-pulsar</artifactId>
    <!-- Spring Boot 3.2.3 默认使用的Pulsar版本 -->
</dependency>
```

### 方案3：创建.mvn/jvm.config文件

在项目根目录创建 `.mvn/jvm.config` 文件：

```
--add-opens java.base/sun.net=ALL-UNNAMED
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.net=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
```

### 方案4：环境变量配置

设置环境变量：

```bash
# Linux/Mac
export JAVA_OPTS="--add-opens java.base/sun.net=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

# Windows
set JAVA_OPTS=--add-opens java.base/sun.net=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED
```

## 参数说明

| 参数 | 作用 |
|------|------|
| `--add-opens java.base/sun.net=ALL-UNNAMED` | 允许访问sun.net包中的内部类 |
| `--add-opens java.base/java.lang=ALL-UNNAMED` | 允许访问java.lang包中的内部类 |
| `--add-opens java.base/java.net=ALL-UNNAMED` | 允许访问java.net包中的内部类 |
| `--add-opens java.base/java.util=ALL-UNNAMED` | 允许访问java.util包中的内部类 |

## 安全考虑

使用 `--add-opens` 参数会降低Java模块系统的安全性，因为它允许访问内部API。这些参数应该：

1. **仅在必要时使用**：只为解决特定的第三方库兼容性问题
2. **最小化权限**：只开放必需的包，避免使用 `ALL-UNNAMED`
3. **临时解决方案**：等待库更新以支持新的Java版本
4. **生产环境谨慎使用**：确保充分测试

## 长期解决方案

1. **等待Pulsar更新**：Apache Pulsar社区正在努力解决Java 17+兼容性问题
2. **考虑替代方案**：如果Pulsar兼容性问题持续存在，可以考虑其他消息队列解决方案
3. **监控更新**：定期检查Spring Boot和Pulsar的更新版本

## 验证解决方案

添加JVM参数后，重新启动应用，如果看到以下日志说明Pulsar正常启动：

```
INFO  - Pulsar client created successfully
INFO  - Connected to pulsar://localhost:6650
```

## 故障排除

### 问题1：参数不生效

**原因**：JVM参数配置位置错误或格式错误

**解决**：
- 确保参数在正确的位置（VM options，不是Program arguments）
- 检查参数格式，确保没有多余的空格或换行

### 问题2：仍然有其他模块访问错误

**解决**：根据错误信息添加相应的 `--add-opens` 参数

```bash
# 示例：如果出现其他包的访问错误
--add-opens java.base/java.io=ALL-UNNAMED
--add-opens java.base/java.security=ALL-UNNAMED
```

### 问题3：Docker环境中参数不生效

**解决**：确保在Dockerfile中正确设置环境变量或在启动命令中包含参数

## 总结

这个问题是Java模块系统与第三方库兼容性的常见问题。通过添加适当的JVM参数，可以临时解决这个问题，直到Pulsar完全支持新的Java版本。

建议优先使用方案1（添加JVM启动参数），这是最直接和有效的解决方案。 