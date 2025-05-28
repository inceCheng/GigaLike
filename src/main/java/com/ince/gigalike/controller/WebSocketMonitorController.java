package com.ince.gigalike.controller;

import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.service.WebSocketMonitorService;
import com.ince.gigalike.utils.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * WebSocket 监控控制器
 */
@RestController
@RequestMapping("/api/websocket/monitor")
@RequiredArgsConstructor
@Tag(name = "WebSocket监控", description = "WebSocket连接监控管理接口")
public class WebSocketMonitorController {
    
    private final WebSocketMonitorService webSocketMonitorService;
    
    /**
     * 获取在线用户列表
     */
    @GetMapping("/online-users")
//    @AuthCheck(roles = {"admin"})
    @Operation(summary = "获取在线用户列表", description = "查看当前所有在线的WebSocket连接用户")
    public BaseResponse<List<Map<String, Object>>> getOnlineUsers() {
        List<Map<String, Object>> onlineUsers = webSocketMonitorService.getOnlineUsers();
        return ResultUtils.success(onlineUsers);
    }
    
    /**
     * 获取连接统计信息
     */
    @GetMapping("/stats")
//    @AuthCheck(roles = {"admin"})
    @Operation(summary = "获取连接统计信息", description = "获取WebSocket连接的各种统计数据")
    public BaseResponse<Map<String, Object>> getConnectionStats() {
        Map<String, Object> stats = webSocketMonitorService.getConnectionStats();
        return ResultUtils.success(stats);
    }
    
    /**
     * 获取指定用户的连接信息
     */
    @GetMapping("/user/{userId}")
//    @AuthCheck(roles = {"admin"})
    @Operation(summary = "获取用户连接信息", description = "查看指定用户的WebSocket连接详细信息")
    public BaseResponse getUserConnectionInfo(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        Map<String, Object> userInfo = webSocketMonitorService.getUserConnectionInfo(userId);
        if (userInfo != null) {
            return ResultUtils.success(userInfo);
        } else {
            return ResultUtils.error(404, "用户连接信息不存在");
        }
    }
    
    /**
     * 强制断开用户连接
     */
    @PostMapping("/disconnect/{userId}")
//    @AuthCheck(roles = {"admin"})
    @Operation(summary = "强制断开用户连接", description = "管理员强制断开指定用户的WebSocket连接")
    public BaseResponse disconnectUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "断开原因") @RequestParam(defaultValue = "管理员操作") String reason) {
        boolean success = webSocketMonitorService.disconnectUser(userId, reason);
        if (success) {
            return ResultUtils.success(true);
        } else {
            return ResultUtils.error(400, "用户未在线或断开失败");
        }
    }
    
    /**
     * 获取连接历史记录
     */
    @GetMapping("/history")
//    @AuthCheck(roles = {"admin"})
    @Operation(summary = "获取连接历史记录", description = "查看WebSocket连接的历史记录")
    public BaseResponse<List<Map<String, Object>>> getConnectionHistory(
            @Parameter(description = "记录数量限制") @RequestParam(defaultValue = "100") int limit) {
        if (limit > 1000) {
            limit = 1000; // 最大1000条
        }
        List<Map<String, Object>> history = webSocketMonitorService.getConnectionHistory(limit);
        return ResultUtils.success(history);
    }
    
    /**
     * 清理无效连接
     */
    @PostMapping("/cleanup")
//    @AuthCheck(roles = {"admin"})
    @Operation(summary = "清理无效连接", description = "清理已断开但未正确移除的WebSocket连接")
    public BaseResponse cleanupInvalidConnections() {
        int cleaned = webSocketMonitorService.cleanupInvalidConnections();
        return ResultUtils.success(cleaned);
    }
    
    /**
     * 实时连接数据（用于前端轮询）
     */
    @GetMapping("/realtime")
//    @AuthCheck(roles = {"admin"})
    @Operation(summary = "获取实时连接数据", description = "获取实时的连接统计信息，适合前端轮询使用")
    public BaseResponse<Map<String, Object>> getRealtimeData() {
        Map<String, Object> stats = webSocketMonitorService.getConnectionStats();
        List<Map<String, Object>> onlineUsers = webSocketMonitorService.getOnlineUsers();
        
        // 添加在线用户的简化信息
        stats.put("onlineUserCount", onlineUsers.size());
        stats.put("onlineUserIds", onlineUsers.stream()
                .map(user -> user.get("userId"))
                .toList());
        
        return ResultUtils.success(stats);
    }
} 