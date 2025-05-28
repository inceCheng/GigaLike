package com.ince.gigalike.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通知 WebSocket 处理器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketHandler implements WebSocketHandler {
    
    private final UserService userService;
    private final ObjectMapper objectMapper;
    
    // 存储用户ID与WebSocket会话的映射
    private static final Map<Long, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();
    
    // 存储连接信息
    private static final Map<Long, ConnectionInfo> CONNECTION_INFO = new ConcurrentHashMap<>();
    
    // 连接历史记录（最多保存1000条）
    private static final List<ConnectionHistory> CONNECTION_HISTORY = Collections.synchronizedList(new ArrayList<>());
    
    // 统计信息
    private static final AtomicLong TOTAL_CONNECTIONS = new AtomicLong(0);
    private static final AtomicLong TOTAL_MESSAGES_SENT = new AtomicLong(0);
    private static final AtomicLong TOTAL_MESSAGES_RECEIVED = new AtomicLong(0);
    
    /**
     * 连接信息内部类
     */
    public static class ConnectionInfo {
        public Long userId;
        public String username;
        public String displayName;
        public LocalDateTime connectTime;
        public String remoteAddress;
        public String userAgent;
        public LocalDateTime lastHeartbeat;
        public AtomicLong messagesSent = new AtomicLong(0);
        public AtomicLong messagesReceived = new AtomicLong(0);
    }
    
    /**
     * 连接历史记录内部类
     */
    public static class ConnectionHistory {
        public Long userId;
        public String username;
        public String action; // CONNECT, DISCONNECT, ERROR
        public LocalDateTime timestamp;
        public String details;
        public String remoteAddress;
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // 从查询参数中获取用户ID
            Long userId = getUserIdFromSession(session);
            if (userId != null) {
                // 验证用户是否存在
                User user = userService.getById(userId);
                if (user != null) {
                    USER_SESSIONS.put(userId, session);
                    
                    // 记录连接信息
                    ConnectionInfo connInfo = new ConnectionInfo();
                    connInfo.userId = userId;
                    connInfo.username = user.getUsername();
                    connInfo.displayName = user.getDisplayName();
                    connInfo.connectTime = LocalDateTime.now();
                    connInfo.lastHeartbeat = LocalDateTime.now();
                    connInfo.remoteAddress = getRemoteAddress(session);
                    connInfo.userAgent = getUserAgent(session);
                    CONNECTION_INFO.put(userId, connInfo);
                    
                    // 记录连接历史
                    addConnectionHistory(userId, user.getUsername(), "CONNECT", 
                            "用户建立WebSocket连接", getRemoteAddress(session));
                    
                    // 更新统计
                    TOTAL_CONNECTIONS.incrementAndGet();
                    
                    log.info("用户 {} ({}) 建立WebSocket连接，来自: {}", 
                            userId, user.getUsername(), getRemoteAddress(session));
                    
                    // 发送连接成功消息
                    sendMessage(session, createMessage("CONNECTED", "连接成功", Map.of(
                            "userId", userId,
                            "connectTime", connInfo.connectTime.toString()
                    )));
                } else {
                    log.warn("无效的用户ID: {}", userId);
                    session.close(CloseStatus.BAD_DATA.withReason("无效的用户ID"));
                }
            } else {
                log.warn("缺少用户ID参数，来自: {}", getRemoteAddress(session));
                session.close(CloseStatus.BAD_DATA.withReason("缺少用户ID参数"));
            }
        } catch (Exception e) {
            log.error("建立WebSocket连接失败", e);
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 处理客户端发送的消息（如心跳包）
        String payload = message.getPayload().toString();
        log.debug("收到WebSocket消息: {}", payload);
        
        // 更新统计
        TOTAL_MESSAGES_RECEIVED.incrementAndGet();
        
        // 更新用户消息统计和心跳时间
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            ConnectionInfo connInfo = CONNECTION_INFO.get(userId);
            if (connInfo != null) {
                connInfo.messagesReceived.incrementAndGet();
                
                // 处理心跳包
                if ("PING".equals(payload)) {
                    connInfo.lastHeartbeat = LocalDateTime.now();
                    sendMessage(session, createMessage("PONG", "心跳响应", Map.of(
                            "serverTime", LocalDateTime.now().toString()
                    )));
                }
            }
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误", exception);
        
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            ConnectionInfo connInfo = CONNECTION_INFO.get(userId);
            if (connInfo != null) {
                addConnectionHistory(userId, connInfo.username, "ERROR", 
                        "传输错误: " + exception.getMessage(), connInfo.remoteAddress);
            }
        }
        
        removeSession(session);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Long userId = getUserIdFromSession(session);
        
        if (userId != null) {
            ConnectionInfo connInfo = CONNECTION_INFO.get(userId);
            if (connInfo != null) {
                addConnectionHistory(userId, connInfo.username, "DISCONNECT", 
                        "连接关闭: " + closeStatus.toString(), connInfo.remoteAddress);
            }
        }
        
        removeSession(session);
        log.info("WebSocket连接关闭: {}, 用户ID: {}", closeStatus, userId);
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    /**
     * 向指定用户发送通知
     */
    public void sendNotificationToUser(Long userId, Object notification) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String message = createMessage("NOTIFICATION", "新通知", notification);
                sendMessage(session, message);
                
                // 更新统计
                ConnectionInfo connInfo = CONNECTION_INFO.get(userId);
                if (connInfo != null) {
                    connInfo.messagesSent.incrementAndGet();
                }
                TOTAL_MESSAGES_SENT.incrementAndGet();
                
                log.debug("向用户 {} 发送通知成功", userId);
            } catch (Exception e) {
                log.error("向用户 {} 发送通知失败", userId, e);
                // 如果发送失败，移除无效的会话
                removeSession(session);
            }
        } else {
            log.debug("用户 {} 未在线或连接已关闭", userId);
        }
    }
    
    /**
     * 向所有在线用户广播消息
     */
    public void broadcastToAllUsers(Object message) {
        String messageStr = createMessage("BROADCAST", "系统广播", message);
        USER_SESSIONS.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    sendMessage(session, messageStr);
                    
                    // 更新统计
                    ConnectionInfo connInfo = CONNECTION_INFO.get(userId);
                    if (connInfo != null) {
                        connInfo.messagesSent.incrementAndGet();
                    }
                    TOTAL_MESSAGES_SENT.incrementAndGet();
                    
                } catch (Exception e) {
                    log.error("向用户 {} 广播消息失败", userId, e);
                    removeSession(session);
                }
            }
        });
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return USER_SESSIONS.size();
    }
    
    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        return session != null && session.isOpen();
    }
    
    /**
     * 获取所有在线用户信息
     */
    public List<Map<String, Object>> getOnlineUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        CONNECTION_INFO.forEach((userId, connInfo) -> {
            if (USER_SESSIONS.containsKey(userId)) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("userId", connInfo.userId);
                userInfo.put("username", connInfo.username);
                userInfo.put("displayName", connInfo.displayName);
                userInfo.put("connectTime", connInfo.connectTime);
                userInfo.put("lastHeartbeat", connInfo.lastHeartbeat);
                userInfo.put("remoteAddress", connInfo.remoteAddress);
                userInfo.put("userAgent", connInfo.userAgent);
                userInfo.put("messagesSent", connInfo.messagesSent.get());
                userInfo.put("messagesReceived", connInfo.messagesReceived.get());
                users.add(userInfo);
            }
        });
        return users;
    }
    
    /**
     * 获取连接统计信息
     */
    public Map<String, Object> getConnectionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("currentConnections", USER_SESSIONS.size());
        stats.put("totalConnections", TOTAL_CONNECTIONS.get());
        stats.put("totalMessagesSent", TOTAL_MESSAGES_SENT.get());
        stats.put("totalMessagesReceived", TOTAL_MESSAGES_RECEIVED.get());
        stats.put("connectionHistory", CONNECTION_HISTORY.size());
        
        // 统计各种状态的连接
        long activeConnections = USER_SESSIONS.values().stream()
                .mapToLong(session -> session.isOpen() ? 1 : 0)
                .sum();
        stats.put("activeConnections", activeConnections);
        stats.put("inactiveConnections", USER_SESSIONS.size() - activeConnections);
        
        return stats;
    }
    
    /**
     * 获取指定用户的连接信息
     */
    public Map<String, Object> getUserConnectionInfo(Long userId) {
        ConnectionInfo connInfo = CONNECTION_INFO.get(userId);
        if (connInfo != null) {
            Map<String, Object> info = new HashMap<>();
            info.put("userId", connInfo.userId);
            info.put("username", connInfo.username);
            info.put("displayName", connInfo.displayName);
            info.put("connectTime", connInfo.connectTime);
            info.put("lastHeartbeat", connInfo.lastHeartbeat);
            info.put("remoteAddress", connInfo.remoteAddress);
            info.put("userAgent", connInfo.userAgent);
            info.put("messagesSent", connInfo.messagesSent.get());
            info.put("messagesReceived", connInfo.messagesReceived.get());
            info.put("isOnline", USER_SESSIONS.containsKey(userId));
            
            WebSocketSession session = USER_SESSIONS.get(userId);
            if (session != null) {
                info.put("sessionId", session.getId());
                info.put("isOpen", session.isOpen());
            }
            
            return info;
        }
        return null;
    }
    
    /**
     * 强制断开用户连接
     */
    public boolean disconnectUser(Long userId, String reason) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                ConnectionInfo connInfo = CONNECTION_INFO.get(userId);
                if (connInfo != null) {
                    addConnectionHistory(userId, connInfo.username, "FORCE_DISCONNECT", 
                            "管理员强制断开: " + reason, connInfo.remoteAddress);
                }
                
                session.close(CloseStatus.NORMAL.withReason(reason));
                return true;
            } catch (Exception e) {
                log.error("强制断开用户 {} 连接失败", userId, e);
                return false;
            }
        }
        return false;
    }
    
    /**
     * 获取连接历史记录
     */
    public List<Map<String, Object>> getConnectionHistory(int limit) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // 获取最近的记录
        int size = CONNECTION_HISTORY.size();
        int start = Math.max(0, size - limit);
        
        for (int i = start; i < size; i++) {
            ConnectionHistory record = CONNECTION_HISTORY.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("userId", record.userId);
            item.put("username", record.username);
            item.put("action", record.action);
            item.put("timestamp", record.timestamp);
            item.put("details", record.details);
            item.put("remoteAddress", record.remoteAddress);
            history.add(item);
        }
        
        return history;
    }
    
    /**
     * 清理无效连接
     */
    public int cleanupInvalidConnections() {
        int cleaned = 0;
        Iterator<Map.Entry<Long, WebSocketSession>> iterator = USER_SESSIONS.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<Long, WebSocketSession> entry = iterator.next();
            WebSocketSession session = entry.getValue();
            
            if (!session.isOpen()) {
                Long userId = entry.getKey();
                iterator.remove();
                CONNECTION_INFO.remove(userId);
                cleaned++;
                
                log.info("清理无效连接：用户ID {}", userId);
            }
        }
        
        return cleaned;
    }
    
    /**
     * 添加连接历史记录
     */
    private void addConnectionHistory(Long userId, String username, String action, String details, String remoteAddress) {
        ConnectionHistory history = new ConnectionHistory();
        history.userId = userId;
        history.username = username;
        history.action = action;
        history.timestamp = LocalDateTime.now();
        history.details = details;
        history.remoteAddress = remoteAddress;
        
        CONNECTION_HISTORY.add(history);
        
        // 保持历史记录数量在1000以内
        if (CONNECTION_HISTORY.size() > 1000) {
            CONNECTION_HISTORY.remove(0);
        }
    }
    
    /**
     * 获取远程地址
     */
    private String getRemoteAddress(WebSocketSession session) {
        try {
            return session.getRemoteAddress() != null ? 
                    session.getRemoteAddress().toString() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    /**
     * 获取用户代理
     */
    private String getUserAgent(WebSocketSession session) {
        try {
            return session.getHandshakeHeaders().getFirst("User-Agent");
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    /**
     * 从会话中获取用户ID
     */
    private Long getUserIdFromSession(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri != null) {
                String query = uri.getQuery();
                if (query != null) {
                    String[] params = query.split("&");
                    for (String param : params) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                            return Long.parseLong(keyValue[1]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析用户ID失败", e);
        }
        return null;
    }
    
    /**
     * 移除会话
     */
    private void removeSession(WebSocketSession session) {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            USER_SESSIONS.remove(userId);
            CONNECTION_INFO.remove(userId);
        }
    }
    
    /**
     * 发送消息
     */
    private void sendMessage(WebSocketSession session, String message) throws IOException {
        synchronized (session) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
    
    /**
     * 创建消息格式
     */
    private String createMessage(String type, String message, Object data) {
        try {
            Map<String, Object> messageMap = Map.of(
                "type", type,
                "message", message,
                "data", data != null ? data : new Object(),
                "timestamp", System.currentTimeMillis()
            );
            return objectMapper.writeValueAsString(messageMap);
        } catch (Exception e) {
            log.error("创建消息失败", e);
            return "{\"type\":\"ERROR\",\"message\":\"消息格式化失败\"}";
        }
    }
} 