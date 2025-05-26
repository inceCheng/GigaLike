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
} 