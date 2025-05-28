package com.ince.gigalike.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ince.gigalike.listener.notification.msg.NotificationEvent;
import com.ince.gigalike.model.dto.NotificationQueryRequest;
import com.ince.gigalike.model.entity.Notification;
import com.ince.gigalike.model.vo.NotificationVO;

/**
 * 通知服务接口
 */
public interface NotificationService extends IService<Notification> {
    
    /**
     * 创建通知
     */
    void createNotification(NotificationEvent event);
    
    /**
     * 分页查询用户通知
     */
    IPage<NotificationVO> getNotificationPage(NotificationQueryRequest request, Long userId);
    
    /**
     * 标记通知为已读
     */
    boolean markAsRead(Long notificationId, Long userId);
    
    /**
     * 批量标记为已读
     */
    boolean markAllAsRead(Long userId);
    
    /**
     * 获取未读通知数量
     */
    int getUnreadCount(Long userId);
    
    /**
     * 删除通知
     */
    boolean deleteNotification(Long notificationId, Long userId);
    
    /**
     * 清理用户的旧通知
     */
    void cleanOldNotifications(Long userId);
} 