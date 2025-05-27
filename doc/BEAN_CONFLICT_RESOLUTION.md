# Bean冲突解决方案 🔧

## 问题描述

在实施JavaScript数字精度问题解决方案时，遇到了Spring Bean定义冲突：

```
BeanDefinitionOverrideException: Invalid bean definition with name 'objectMapper' 
defined in class path resource [com/ince/gigalike/config/WebMvcConfig.class]: 
Cannot register bean definition for bean 'objectMapper' since there is already 
[Root bean: class [null]; scope=; abstract=false; lazyInit=null; autowireMode=3; 
dependencyCheck=0; autowireCandidate=true; primary=true; factoryBeanName=jacksonConfig; 
factoryMethodName=objectMapper; initMethodNames=null; destroyMethodNames=[(inferred)]; 
defined in class path resource [com/ince/gigalike/config/JacksonConfig.class]] bound.
```

## 根本原因

1. **重复Bean定义**：项目中已存在`WebMvcConfig.objectMapper()`方法
2. **新增配置冲突**：新创建的`JacksonConfig.objectMapper()`与现有Bean同名
3. **Spring默认行为**：Spring Boot默认不允许Bean定义覆盖

## 解决方案

### 方案一：集成配置（采用）

将新的Long序列化配置集成到现有的`WebMvcConfig`中：

**修改前的WebMvcConfig：**
```java
@Bean
public ObjectMapper objectMapper() {
    JavaTimeModule module = new JavaTimeModule();
    LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    module.addSerializer(LocalDateTime.class, localDateTimeSerializer);

    return Jackson2ObjectMapperBuilder.json()
            .modules(module)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .timeZone(TimeZone.getTimeZone("Asia/Shanghai"))
            .build();
}
```

**修改后的WebMvcConfig：**
```java
@Bean
@Primary
public ObjectMapper objectMapper() {
    // 时间模块配置
    JavaTimeModule timeModule = new JavaTimeModule();
    LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    timeModule.addSerializer(LocalDateTime.class, localDateTimeSerializer);

    // Long类型序列化模块配置（解决JavaScript数字精度问题）
    SimpleModule longModule = new SimpleModule();
    longModule.addSerializer(Long.class, ToStringSerializer.instance);
    longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

    return Jackson2ObjectMapperBuilder.json()
            .modules(timeModule, longModule)  // 注册两个模块
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .timeZone(TimeZone.getTimeZone("Asia/Shanghai"))
            .build();
}
```

**关键变化：**
1. 添加了`@Primary`注解确保Bean优先级
2. 创建了独立的`longModule`处理Long序列化
3. 在`modules()`方法中注册两个模块
4. 删除了冲突的`JacksonConfig`类

### 方案二：允许Bean覆盖（不推荐）

在`application.yml`中添加配置：
```yaml
spring:
  main:
    allow-bean-definition-overriding: true
```

**缺点：**
- 可能隐藏其他Bean冲突问题
- 不明确哪个Bean会生效
- 增加调试难度

### 方案三：重命名Bean（不推荐）

给新的Bean使用不同的名称：
```java
@Bean("customObjectMapper")
public ObjectMapper customObjectMapper() {
    // 配置...
}
```

**缺点：**
- 需要手动指定使用哪个ObjectMapper
- 可能导致配置不一致

## 实施步骤

### 1. 删除冲突的配置类
```bash
rm src/main/java/com/ince/gigalike/config/JacksonConfig.java
```

### 2. 修改现有配置
在`WebMvcConfig.java`中：
- 添加必要的import
- 修改`objectMapper()`方法
- 添加`@Primary`注解

### 3. 验证配置
创建测试接口验证Long类型序列化：
```java
@GetMapping("/test/long-precision")
public BaseResponse<Map<String, Object>> testLongPrecision() {
    Map<String, Object> result = new HashMap<>();
    result.put("originalLong", 1926924684188102658L);
    return ResultUtils.success(result);
}
```

## 验证方法

### 1. 启动应用
确保应用能正常启动，无Bean冲突错误。

### 2. 测试API
访问测试接口：
```bash
curl http://localhost:8123/api/test/long-precision
```

期望返回：
```json
{
  "code": 0,
  "data": {
    "originalLong": "1926924684188102658"  // 字符串格式
  }
}
```

### 3. 前端验证
在浏览器控制台测试：
```javascript
fetch('/api/test/long-precision')
  .then(response => response.json())
  .then(data => {
    console.log('Long值类型:', typeof data.data.originalLong);
    console.log('Long值内容:', data.data.originalLong);
  });
```

## 最佳实践

### 1. 配置整合
- 将相关配置集中在一个类中
- 避免功能重复的配置类
- 使用清晰的方法命名

### 2. Bean管理
- 使用`@Primary`明确Bean优先级
- 避免同名Bean定义
- 合理使用`@Qualifier`注解

### 3. 模块化配置
```java
// 推荐：模块化配置
@Bean
@Primary
public ObjectMapper objectMapper() {
    return Jackson2ObjectMapperBuilder.json()
            .modules(
                createTimeModule(),     // 时间处理模块
                createLongModule(),     // Long序列化模块
                createCustomModule()    // 其他自定义模块
            )
            .build();
}

private JavaTimeModule createTimeModule() {
    // 时间模块配置
}

private SimpleModule createLongModule() {
    // Long序列化模块配置
}
```

### 4. 配置文档
- 记录每个配置的目的
- 说明配置间的依赖关系
- 提供验证方法

## 总结

通过将Long序列化配置集成到现有的`WebMvcConfig`中，我们成功解决了Bean冲突问题，同时保持了配置的一致性和可维护性。这种方案：

1. **避免冲突**：消除了Bean定义重复问题
2. **功能完整**：保留了原有的时间序列化功能
3. **易于维护**：配置集中在一个地方
4. **性能优化**：避免了多个ObjectMapper实例

建议在类似情况下优先考虑配置整合方案，而不是允许Bean覆盖或创建新的配置类。 