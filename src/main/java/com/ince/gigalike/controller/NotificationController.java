package com.ince.gigalike.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.model.dto.NotificationQueryRequest;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.model.vo.NotificationVO;
import com.ince.gigalike.service.NotificationService;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.utils.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "通知管理", description = "通知相关接口")
public class NotificationController {
    
    private final NotificationService notificationService;

    private final UserService userService;
    
    /**
     * 分页查询用户通知
     */
    @PostMapping("/list")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "分页查询用户通知")
    public BaseResponse<IPage<NotificationVO>> getNotificationList(
            @RequestBody NotificationQueryRequest request,
            HttpServletRequest httpRequest) {
        
        User loginUser = userService.getLoginUser(httpRequest);
        IPage<NotificationVO> result = notificationService.getNotificationPage(request, loginUser.getId());
        return ResultUtils.success(result);
    }
    
    /**
     * 标记通知为已读
     */
    @PostMapping("/read/{id}")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "标记通知为已读")
    public BaseResponse<Boolean> markAsRead(
            @PathVariable("id") Long notificationId,
            HttpServletRequest request) {
        
        User loginUser = userService.getLoginUser(request);
        boolean result = notificationService.markAsRead(notificationId, loginUser.getId());
        return ResultUtils.success(result);
    }
    
    /**
     * 批量标记为已读
     */
    @PostMapping("/read/all")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "批量标记为已读")
    public BaseResponse<Boolean> markAllAsRead(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = notificationService.markAllAsRead(loginUser.getId());
        return ResultUtils.success(result);
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread/count")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "获取未读通知数量")
    public BaseResponse<Integer> getUnreadCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        int count = notificationService.getUnreadCount(loginUser.getId());
        return ResultUtils.success(count);
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "删除通知")
    public BaseResponse<Boolean> deleteNotification(
            @PathVariable("id") Long notificationId,
            HttpServletRequest request) {
        
        User loginUser = userService.getLoginUser(request);
        boolean result = notificationService.deleteNotification(notificationId, loginUser.getId());
        return ResultUtils.success(result);
    }
} 