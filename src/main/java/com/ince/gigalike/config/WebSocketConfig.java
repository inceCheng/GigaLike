package com.ince.gigalike.config;


import com.ince.gigalike.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类
 */

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotificationWebSocketHandler notificationWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册通知 WebSocket 处理器
        registry.addHandler(notificationWebSocketHandler, "/ws/notification")
                .setAllowedOriginPatterns("*") // 使用 allowedOriginPatterns 替代 allowedOrigins 以支持Spring Boot 3.x
                .withSockJS(); // 支持 SockJS 降级
    }
}