package com.ince.gigalike.service.Impl;

import com.ince.gigalike.service.WebSocketMonitorService;
import com.ince.gigalike.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * WebSocket 监控服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketMonitorServiceImpl implements WebSocketMonitorService {
    
    private final NotificationWebSocketHandler webSocketHandler;
    
    @Override
    public List<Map<String, Object>> getOnlineUsers() {
        return webSocketHandler.getOnlineUsers();
    }
    
    @Override
    public Map<String, Object> getConnectionStats() {
        return webSocketHandler.getConnectionStats();
    }
    
    @Override
    public Map<String, Object> getUserConnectionInfo(Long userId) {
        return webSocketHandler.getUserConnectionInfo(userId);
    }
    
    @Override
    public boolean disconnectUser(Long userId, String reason) {
        log.info("管理员请求断开用户 {} 的连接，原因: {}", userId, reason);
        return webSocketHandler.disconnectUser(userId, reason);
    }
    
    @Override
    public List<Map<String, Object>> getConnectionHistory(int limit) {
        return webSocketHandler.getConnectionHistory(limit);
    }
    
    @Override
    public int cleanupInvalidConnections() {
        log.info("开始清理无效的WebSocket连接");
        int cleaned = webSocketHandler.cleanupInvalidConnections();
        log.info("清理完成，共清理了 {} 个无效连接", cleaned);
        return cleaned;
    }
} 