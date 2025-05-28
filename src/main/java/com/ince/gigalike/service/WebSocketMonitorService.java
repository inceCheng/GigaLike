package com.ince.gigalike.service;

import java.util.List;
import java.util.Map;

/**
 * WebSocket 监控服务
 */
public interface WebSocketMonitorService {
    
    /**
     * 获取在线用户列表
     */
    List<Map<String, Object>> getOnlineUsers();
    
    /**
     * 获取连接统计信息
     */
    Map<String, Object> getConnectionStats();
    
    /**
     * 获取指定用户的连接信息
     */
    Map<String, Object> getUserConnectionInfo(Long userId);
    
    /**
     * 强制断开用户连接
     */
    boolean disconnectUser(Long userId, String reason);
    
    /**
     * 获取连接历史记录
     */
    List<Map<String, Object>> getConnectionHistory(int limit);
    
    /**
     * 清理无效连接
     */
    int cleanupInvalidConnections();
} 