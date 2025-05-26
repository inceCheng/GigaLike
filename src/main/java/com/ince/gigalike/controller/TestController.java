package com.ince.gigalike.controller;

import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.utils.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于演示AOP登录验证功能
 */
@RestController
@RequestMapping("/test")
@Tag(name = "测试接口", description = "用于测试AOP登录验证功能")
public class TestController {

    @Resource
    private UserService userService;

    /**
     * 公开接口 - 无需登录
     */
    @GetMapping("/public")
    @Operation(summary = "公开接口", description = "无需登录即可访问")
    public BaseResponse<String> publicEndpoint() {
        return ResultUtils.success("这是一个公开接口，无需登录即可访问");
    }

    /**
     * 需要登录的接口
     */
    @GetMapping("/private")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "私有接口", description = "需要登录才能访问")
    public BaseResponse<String> privateEndpoint(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success("欢迎 " + loginUser.getUsername() + "，这是一个需要登录的接口");
    }

    /**
     * 需要管理员权限的接口
     */
    @GetMapping("/admin")
    @AuthCheck(mustLogin = true, roles = {"ADMIN"})
    @Operation(summary = "管理员接口", description = "需要管理员权限才能访问")
    public BaseResponse<String> adminEndpoint(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success("欢迎管理员 " + loginUser.getUsername() + "，这是一个需要管理员权限的接口");
    }

    /**
     * 需要编辑权限的接口
     */
    @GetMapping("/editor")
    @AuthCheck(mustLogin = true, roles = {"EDITOR", "ADMIN"})
    @Operation(summary = "编辑接口", description = "需要编辑或管理员权限才能访问")
    public BaseResponse<String> editorEndpoint(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success("欢迎 " + loginUser.getUsername() + "，这是一个需要编辑权限的接口");
    }

    /**
     * 测试Long类型序列化
     */
    @GetMapping("/long-precision")
    public BaseResponse<Map<String, Object>> testLongPrecision() {
        Map<String, Object> result = new HashMap<>();
        
        // 测试大数值
        Long largeId = 1926924684188102658L;
        Long maxSafeInteger = 9007199254740991L;
        
        result.put("originalLong", largeId);
        result.put("maxSafeInteger", maxSafeInteger);
        result.put("isOverMaxSafe", largeId > maxSafeInteger);
        result.put("testMessage", "如果Long类型正确序列化为字符串，originalLong应该显示为字符串格式");
        
        return ResultUtils.success(result);
    }
    
    /**
     * 测试各种数据类型
     */
    @GetMapping("/data-types")
    public BaseResponse<Map<String, Object>> testDataTypes() {
        Map<String, Object> result = new HashMap<>();
        
        result.put("longValue", 1926924684188102658L);
        result.put("intValue", 123456);
        result.put("stringValue", "test string");
        result.put("booleanValue", true);
        result.put("doubleValue", 123.456);
        
        return ResultUtils.success(result);
    }
} 