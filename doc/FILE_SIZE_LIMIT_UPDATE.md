# 文件上传大小限制更新说明 📁

## 更新概述

将用户上传图片的大小限制从 **5MB** 提升到 **10MB**，以支持更高质量的图片上传需求。

## 修改内容

### 1. Spring Boot 配置文件更新

**文件**: `src/main/resources/application.yml`

```yaml
spring:
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB      # 单个文件最大10MB（原5MB）
      max-request-size: 20MB   # 请求最大20MB（原10MB）

server:
  # 添加Tomcat配置
  tomcat:
    # 设置Tomcat的文件上传限制
    max-http-form-post-size: 10MB
    max-swallow-size: 10MB
```

### 2. 业务逻辑代码更新

**文件**: `src/main/java/com/ince/gigalike/service/Impl/FileUploadServiceImpl.java`

```java
/**
 * 最大文件大小 10MB（原5MB）
 */
private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

// 文件大小验证
if (file.getSize() > MAX_FILE_SIZE) {
    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过10MB");
}
```

### 3. 文档更新

**文件**: `doc/IMAGE_UPLOAD_GUIDE.md`

- 功能特性说明：文件安全限制从5MB更新为10MB
- 配置示例：更新Spring配置示例

## 配置层级说明

### 多层限制机制

为确保文件上传正常工作，需要在多个层级设置限制：

| 层级 | 配置项 | 作用 | 设置值 |
|------|--------|------|--------|
| **Spring Boot** | `spring.servlet.multipart.max-file-size` | Spring框架层面的单文件大小限制 | 10MB |
| **Spring Boot** | `spring.servlet.multipart.max-request-size` | Spring框架层面的请求总大小限制 | 20MB |
| **Tomcat** | `server.tomcat.max-http-form-post-size` | Tomcat容器的POST请求大小限制 | 10MB |
| **Tomcat** | `server.tomcat.max-swallow-size` | Tomcat容器的请求体大小限制 | 10MB |
| **业务逻辑** | `MAX_FILE_SIZE` | 应用层面的业务验证 | 10MB |

### 配置优先级

1. **Tomcat容器级别**：最底层限制，如果超过会直接被容器拒绝
2. **Spring框架级别**：框架层面的限制，在业务逻辑之前生效
3. **业务逻辑级别**：应用自定义的验证逻辑

## 错误处理

### 常见错误类型

1. **FileSizeLimitExceededException**
   - 原因：文件大小超过Tomcat容器限制
   - 解决：检查`server.tomcat.max-http-form-post-size`配置

2. **MaxUploadSizeExceededException**
   - 原因：文件大小超过Spring框架限制
   - 解决：检查`spring.servlet.multipart.max-file-size`配置

3. **BusinessException**
   - 原因：文件大小超过业务逻辑限制
   - 解决：检查`FileUploadServiceImpl`中的`MAX_FILE_SIZE`常量

### 错误日志示例

```
org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException: 
The field file exceeds its maximum permitted size of 1048576 bytes.
```

这个错误表明Tomcat的限制没有正确配置，需要检查Tomcat相关配置。

## 测试验证

### 验证步骤

1. **重启应用**：确保配置生效
2. **上传测试**：
   - 上传5MB以下文件：应该成功
   - 上传5-10MB文件：应该成功（新增支持）
   - 上传10MB以上文件：应该被拒绝

### 测试用例

```bash
# 创建测试文件
dd if=/dev/zero of=test_5mb.jpg bs=1M count=5    # 5MB文件
dd if=/dev/zero of=test_8mb.jpg bs=1M count=8    # 8MB文件
dd if=/dev/zero of=test_12mb.jpg bs=1M count=12  # 12MB文件（应该失败）
```

## 性能考虑

### 内存使用

- **临时存储**：上传的文件会临时存储在内存或临时目录
- **并发影响**：多个10MB文件同时上传可能增加内存压力
- **建议**：监控应用内存使用情况

### 网络传输

- **上传时间**：10MB文件在慢网络下可能需要较长时间
- **超时设置**：确保COS客户端的超时时间足够（当前设置4秒）
- **用户体验**：考虑添加上传进度显示

## 后续优化建议

### 1. 动态配置

可以考虑将文件大小限制配置化：

```yaml
# application.yml
file-upload:
  max-size: 10MB
  allowed-types: jpg,png,gif,webp
```

### 2. 分片上传

对于更大的文件，可以考虑实现分片上传：

```java
// 大文件分片上传逻辑
public String uploadLargeFile(MultipartFile file, int chunkSize) {
    // 分片上传实现
}
```

### 3. 压缩优化

在上传前对图片进行压缩：

```java
// 图片压缩逻辑
public MultipartFile compressImage(MultipartFile originalFile) {
    // 压缩实现
}
```

## 总结

通过这次更新，我们成功将文件上传限制从5MB提升到10MB，涉及的修改包括：

1. ✅ **配置文件更新**：Spring Boot和Tomcat配置
2. ✅ **业务逻辑更新**：文件大小验证逻辑
3. ✅ **文档更新**：相关说明文档
4. ✅ **错误处理**：完善的错误提示和处理机制

这个更新将为用户提供更好的图片上传体验，支持更高质量的图片内容。 