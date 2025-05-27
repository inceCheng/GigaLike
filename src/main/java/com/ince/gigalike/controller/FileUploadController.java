package com.ince.gigalike.controller;

import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.utils.ResultUtils;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.FileUploadService;
import com.ince.gigalike.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/file")
@Tag(name = "文件上传接口", description = "文件上传相关操作")
@Slf4j
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    @Resource
    private UserService userService;

    /**
     * 上传博客图片
     */
    @PostMapping("/upload/blog-image")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "上传博客图片", description = "上传博客图片到COS对象存储")
    public BaseResponse<String> uploadBlogImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "blogId", required = false) Long blogId,
            @RequestParam("title") String title,
            HttpServletRequest request) {

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 上传图片
        String imageUrl = fileUploadService.uploadBlogImage(file, loginUser.getId(), blogId, title);

        return ResultUtils.success(imageUrl);
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/upload/avatar")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "上传用户头像", description = "上传用户头像到COS对象存储，支持jpg、png、gif、webp格式，最大5MB")
    public BaseResponse<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 上传头像
        String avatarUrl = fileUploadService.uploadUserAvatar(file, loginUser.getId());

        return ResultUtils.success(avatarUrl);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "删除文件", description = "删除COS对象存储中的文件")
    public BaseResponse<Boolean> deleteFile(
            @RequestParam("fileUrl") String fileUrl,
            HttpServletRequest request) {

        // 删除文件
        Boolean result = fileUploadService.deleteFile(fileUrl);

        return ResultUtils.success(result);
    }
} 