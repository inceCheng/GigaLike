package com.ince.gigalike.listener.notification.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements Serializable {
    
    /**
     * 接收通知的用户ID
     */
    private Long userId;
    
    /**
     * 发送通知的用户ID（系统通知时为NULL）
     */
    private Long senderId;
    
    /**
     * 通知类型：LIKE, COMMENT, FOLLOW, SYSTEM
     */
    private String type;
    
    /**
     * 关联的资源ID（如博客ID、评论ID等）
     */
    private Long relatedId;
    
    /**
     * 关联资源类型：BLOG, COMMENT, USER
     */
    private String relatedType;
    
    /**
     * 额外数据（如博客标题、用户名等）
     */
    private Map<String, Object> extraData;
    
    /**
     * 事件发生时间
     */
    private LocalDateTime eventTime;
} 