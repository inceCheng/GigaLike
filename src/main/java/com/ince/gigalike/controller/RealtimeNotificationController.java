package com.ince.gigalike.controller;

import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.utils.ResultUtils;
import com.ince.gigalike.websocket.NotificationWebSocketHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 实时通知控制器
 */
@RestController
@RequestMapping("/api/realtime")
@RequiredArgsConstructor
@Tag(name = "实时通知管理", description = "WebSocket实时通知相关接口")
public class RealtimeNotificationController {
    
    private final NotificationWebSocketHandler webSocketHandler;
    private final UserService userService;
    
    /**
     * 获取在线状态
     */
    @GetMapping("/status")
//    @AuthCheck(mustLogin = true)
    @Operation(summary = "获取用户在线状态")
    public BaseResponse<Map<String, Object>> getOnlineStatus(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        Map<String, Object> status = new HashMap<>();
        status.put("isOnline", webSocketHandler.isUserOnline(loginUser.getId()));
        status.put("onlineUserCount", webSocketHandler.getOnlineUserCount());
        status.put("userId", loginUser.getId());
        
        return ResultUtils.success(status);
    }
    
    /**
     * 发送测试通知（仅管理员）
     */
    @PostMapping("/test")
//    @AuthCheck(roles = {"admin"})
    @Operation(summary = "发送测试通知")
    public BaseResponse<Boolean> sendTestNotification(
            @RequestParam Long targetUserId,
            @RequestParam(defaultValue = "这是一条测试通知") String message,
            HttpServletRequest request) {
        
        Map<String, Object> testData = new HashMap<>();
        testData.put("message", message);
        testData.put("timestamp", System.currentTimeMillis());
        
        webSocketHandler.sendNotificationToUser(targetUserId, testData);
        
        return ResultUtils.success(true);
    }
    
    /**
     * 系统广播（仅管理员）
     */
    @PostMapping("/broadcast")
//    @AuthCheck(roles = {"admin"})
    @Operation(summary = "系统广播消息")
    public BaseResponse<Boolean> broadcastMessage(
            @RequestParam String message,
            HttpServletRequest request) {
        
        Map<String, Object> broadcastData = new HashMap<>();
        broadcastData.put("message", message);
        broadcastData.put("timestamp", System.currentTimeMillis());
        broadcastData.put("type", "SYSTEM_BROADCAST");
        
        webSocketHandler.broadcastToAllUsers(broadcastData);
        
        return ResultUtils.success(true);
    }
    
    /**
     * 获取WebSocket连接信息
     */
    @GetMapping("/connection-info")
    @Operation(summary = "获取WebSocket连接信息")
    public BaseResponse<Map<String, Object>> getConnectionInfo(HttpServletRequest request) {
        Map<String, Object> info = new HashMap<>();
        
        // 获取当前请求的schema和host信息
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        
        // 构建WebSocket URL - 包含context-path
        String wsScheme = "https".equals(scheme) ? "wss" : "ws";
        String baseUrl = String.format("%s://%s", wsScheme, serverName);
        if (serverPort != 80 && serverPort != 443) {
            baseUrl += ":" + serverPort;
        }
        
        // WebSocket路径包含context-path
        String websocketPath = contextPath + "/ws/notification";
        String websocketUrl = baseUrl + websocketPath;
        
        info.put("websocketUrl", websocketPath);  // 相对路径
        info.put("fullWebsocketUrl", websocketUrl);  // 完整URL
        info.put("connectionParams", "userId");
        info.put("example", websocketUrl + "?userId=123");
        info.put("supportsSockJS", true);
        info.put("sockJSUrl", websocketPath);
        
        // 添加调试信息
        info.put("contextPath", contextPath);
        info.put("currentDomain", serverName);
        info.put("currentPort", serverPort);
        info.put("scheme", scheme);
        
        return ResultUtils.success(info);
    }
} 