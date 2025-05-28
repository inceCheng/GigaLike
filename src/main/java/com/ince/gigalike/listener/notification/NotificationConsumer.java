package com.ince.gigalike.listener.notification;

import com.ince.gigalike.listener.notification.msg.NotificationEvent;
import com.ince.gigalike.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知消息消费者
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    
    private final NotificationService notificationService;
    
    /**
     * 处理通知事件
     */
    @PulsarListener(
            subscriptionName = "notification-subscription",
            topics = "notification-topic",
            schemaType = SchemaType.JSON,
            subscriptionType = SubscriptionType.Shared
    )
    @Transactional(rollbackFor = Exception.class)
    public void processNotification(Message<NotificationEvent> message) {
        try {
            NotificationEvent event = message.getValue();
            log.info("收到通知事件：userId={}, type={}, relatedId={}", 
                    event.getUserId(), event.getType(), event.getRelatedId());
            
            // 创建通知
            notificationService.createNotification(event);
            
            log.info("通知处理成功：messageId={}", message.getMessageId());
            
        } catch (Exception e) {
            log.error("处理通知事件失败：messageId={}", message.getMessageId(), e);
            throw e; // 重新抛出异常，触发重试机制
        }
    }
} 