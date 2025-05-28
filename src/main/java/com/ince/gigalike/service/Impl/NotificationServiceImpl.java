package com.ince.gigalike.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ince.gigalike.enums.ErrorCode;
import com.ince.gigalike.exception.BusinessException;
import com.ince.gigalike.listener.notification.msg.NotificationEvent;
import com.ince.gigalike.mapper.NotificationMapper;
import com.ince.gigalike.model.dto.NotificationQueryRequest;
import com.ince.gigalike.model.entity.Notification;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.model.vo.NotificationVO;
import com.ince.gigalike.service.NotificationService;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final UserService userService;
    private final NotificationWebSocketHandler webSocketHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNotification(NotificationEvent event) {
        // 1. 防止自己给自己发通知
        if (event.getSenderId() != null && event.getSenderId().equals(event.getUserId())) {
            log.debug("跳过自己给自己的通知：userId={}, senderId={}", event.getUserId(), event.getSenderId());
            return;
        }

        // 2. 生成通知内容
        Map<String, String> notificationContent = generateNotificationContent(event);

        // 3. 创建通知记录
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setSenderId(event.getSenderId());
        notification.setType(event.getType());
        notification.setTitle(notificationContent.get("title"));
        notification.setContent(notificationContent.get("content"));
        notification.setRelatedId(event.getRelatedId());
        notification.setRelatedType(event.getRelatedType());
        notification.setIsRead(0);
        notification.setExtraData(event.getExtraData());
        notification.setCreateTime(new Date());
        notification.setUpdateTime(new Date());

        // 4. 保存通知
        this.save(notification);

        // 5. 实时推送通知给用户
        try {
            NotificationVO notificationVO = convertToVO(notification);
            webSocketHandler.sendNotificationToUser(event.getUserId(), notificationVO);
            log.debug("实时推送通知成功：userId={}, type={}", event.getUserId(), event.getType());
        } catch (Exception e) {
            log.error("实时推送通知失败：userId={}, type={}", event.getUserId(), event.getType(), e);
            // 推送失败不影响通知的保存
        }

        log.info("创建通知成功：userId={}, type={}, relatedId={}",
                event.getUserId(), event.getType(), event.getRelatedId());
    }

    @Override
    public IPage<NotificationVO> getNotificationPage(NotificationQueryRequest request, Long userId) {
        // 参数验证
        if (request.getCurrent() < 1) {
            request.setCurrent(1);
        }
        if (request.getPageSize() < 1 || request.getPageSize() > 100) {
            request.setPageSize(10);
        }

        // 创建分页对象
        Page<Notification> page = new Page<>(request.getCurrent(), request.getPageSize());

        // 查询通知（包含发送者信息）
        IPage<Notification> notificationPage = baseMapper.selectNotificationPageWithSender(
                page, userId, request.getIsRead(), request.getType());

        // 转换为VO
        return notificationPage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long notificationId, Long userId) {
        // 查询通知
        Notification notification = this.getById(notificationId);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "通知不存在");
        }

        // 验证权限
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作此通知");
        }

        // 如果已经是已读状态，直接返回
        if (notification.getIsRead() == 1) {
            return true;
        }

        // 标记为已读
        notification.setIsRead(1);
        notification.setReadTime(new Date());
        notification.setUpdateTime(new Date());

        return this.updateById(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAllAsRead(Long userId) {
        return baseMapper.markAllAsRead(userId) >= 0;
    }

    @Override
    public int getUnreadCount(Long userId) {
        return baseMapper.countUnreadByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNotification(Long notificationId, Long userId) {
        // 查询通知
        Notification notification = this.getById(notificationId);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "通知不存在");
        }

        // 验证权限
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作此通知");
        }

        return this.removeById(notificationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanOldNotifications(Long userId) {
        // 保留最近的1000条通知
        int keepCount = 1000;
        int deletedCount = baseMapper.deleteOldNotifications(userId, keepCount);

        if (deletedCount > 0) {
            log.info("清理用户 {} 的旧通知，删除了 {} 条", userId, deletedCount);
        }
    }

    /**
     * 生成通知内容
     */
    private Map<String, String> generateNotificationContent(NotificationEvent event) {
        Map<String, String> result = new HashMap<>();

        // 获取发送者信息
        String senderName = "系统";
        if (event.getSenderId() != null) {
            User sender = userService.getById(event.getSenderId());
            if (sender != null) {
                senderName = sender.getDisplayName() != null ? sender.getDisplayName() : sender.getUsername();
            }
        }

        // 根据通知类型生成内容
        switch (event.getType()) {
            case "LIKE":
                result.put("title", "收到新的点赞");
                String blogTitle = (String) event.getExtraData().getOrDefault("blogTitle", "你的文章");
                result.put("content", senderName + " 点赞了你的文章《" + blogTitle + "》");
                break;
            case "COMMENT":
                result.put("title", "收到新的评论");
                String commentBlogTitle = (String) event.getExtraData().getOrDefault("blogTitle", "你的文章");
                result.put("content", senderName + " 评论了你的文章《" + commentBlogTitle + "》");
                break;
            case "FOLLOW":
                result.put("title", "收到新的关注");
                result.put("content", senderName + " 关注了你");
                break;
            case "SYSTEM":
                result.put("title", "系统通知");
                result.put("content", (String) event.getExtraData().getOrDefault("content", "系统消息"));
                break;
            default:
                result.put("title", "新通知");
                result.put("content", "您有一条新通知");
        }

        return result;
    }

    /**
     * 转换为VO对象
     */
    private NotificationVO convertToVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        BeanUtils.copyProperties(notification, vo);

        // 设置发送者信息
        if (notification.getSenderId() != null) {
            User sender = userService.getById(notification.getSenderId());
            if (sender != null) {
                NotificationVO.SenderInfo senderInfo = new NotificationVO.SenderInfo();
                senderInfo.setId(sender.getId());
                senderInfo.setUsername(sender.getUsername());
                senderInfo.setDisplayName(sender.getDisplayName());
                senderInfo.setAvatarUrl(sender.getAvatarUrl());
                vo.setSender(senderInfo);
            }
        }

        return vo;
    }
}