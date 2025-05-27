# AOP 登录验证使用指南

## 概述

本项目实现了基于AOP的登录状态校验功能，使用Redis存储Session实现分布式登录状态共享。通过`@AuthCheck`注解可以轻松为接口添加登录验证和权限控制。

## 功能特性

- ✅ 基于AOP的无侵入式登录验证
- ✅ 支持Redis分布式Session共享
- ✅ 支持角色权限验证
- ✅ 自动返回"未登录"错误响应
- ✅ 详细的日志记录
- ✅ 灵活的注解配置

## 核心组件

### 1. 注解定义 - `@AuthCheck`

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    /**
     * 是否必须登录，默认为true
     */
    boolean mustLogin() default true;
    
    /**
     * 可选的角色要求
     */
    String[] roles() default {};
}
```

### 2. AOP切面 - `AuthInterceptor`

负责拦截带有`@AuthCheck`注解的方法，进行登录状态和权限验证。

### 3. 异常处理

- 未登录：返回`ErrorCode.NOT_LOGIN_ERROR`
- 权限不足：返回`ErrorCode.NO_AUTH_ERROR`

## 使用方法

### 1. 基本登录验证

```java
@GetMapping("/user/profile")
@AuthCheck(mustLogin = true)
public BaseResponse<UserVO> getUserProfile(HttpServletRequest request) {
    // 方法执行前会自动验证登录状态
    User loginUser = userService.getLoginUser(request);
    return ResultUtils.success(userVO);
}
```

### 2. 角色权限验证

```java
@PostMapping("/admin/users")
@AuthCheck(mustLogin = true, roles = {"ADMIN"})
public BaseResponse<String> manageUsers() {
    // 只有ADMIN角色的用户才能访问
    return ResultUtils.success("管理员操作成功");
}
```

### 3. 多角色权限验证

```java
@GetMapping("/content/edit")
@AuthCheck(mustLogin = true, roles = {"EDITOR", "ADMIN"})
public BaseResponse<String> editContent() {
    // EDITOR或ADMIN角色的用户都可以访问
    return ResultUtils.success("内容编辑页面");
}
```

### 4. 类级别注解

```java
@RestController
@RequestMapping("/admin")
@AuthCheck(mustLogin = true, roles = {"ADMIN"})
public class AdminController {
    // 整个Controller的所有方法都需要ADMIN权限
}
```

## 配置说明

### 1. 依赖配置

确保`pom.xml`中包含以下依赖：

```xml
<!-- Spring AOP -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<!-- Spring Session Redis -->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

### 2. AOP配置

```java
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig {
}
```

## 测试接口

项目提供了测试接口来验证AOP功能：

- `GET /test/public` - 公开接口，无需登录
- `GET /test/private` - 需要登录
- `GET /test/admin` - 需要ADMIN权限
- `GET /test/editor` - 需要EDITOR或ADMIN权限

## 错误响应示例

### 未登录错误

```json
{
    "code": 40100,
    "message": "未登录",
    "data": null
}
```

### 权限不足错误

```json
{
    "code": 40101,
    "message": "无权限",
    "data": null
}
```

## 日志记录

AOP切面会记录详细的日志信息：

```
INFO  - 用户 testuser 通过权限验证，访问路径: /user/profile
WARN  - 用户未登录，访问路径: /user/profile
WARN  - 用户权限不足，用户角色: USER, 需要角色: ADMIN, 访问路径: /admin/users
```

## 最佳实践

1. **合理使用注解**：对于需要登录的接口统一添加`@AuthCheck`注解
2. **角色设计**：建议使用清晰的角色命名，如`USER`、`EDITOR`、`ADMIN`
3. **异常处理**：确保全局异常处理器能正确处理登录相关异常
4. **日志监控**：关注登录验证相关的日志，及时发现异常访问

## 注意事项

1. 确保Redis服务正常运行
2. Session配置正确
3. 用户角色字段数据格式正确
4. AOP代理配置正确启用

## 扩展功能

可以根据需要扩展以下功能：

- IP白名单验证
- 接口访问频率限制
- 用户状态验证（如账号是否被禁用）
- 更细粒度的权限控制 