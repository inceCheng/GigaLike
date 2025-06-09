# 多阶段构建 - 构建阶段
FROM maven:3.9.6-openjdk-21 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 构建应用（跳过测试以加快构建速度）
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 创建非root用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 从构建阶段复制jar文件
COPY --from=builder /app/target/*.jar app.jar

# 更改文件所有者
RUN chown appuser:appuser app.jar

# 切换到非root用户
USER appuser

# 暴露端口
EXPOSE 8080

# 设置JVM参数和启动命令
ENTRYPOINT ["java", \
    "--add-opens", "java.base/sun.net=ALL-UNNAMED", \
    "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
    "--add-opens", "java.base/java.net=ALL-UNNAMED", \
    "-Xmx512m", \
    "-Xms256m", \
    "-jar", \
    "app.jar", \
    "--spring.profiles.active=docker"] 