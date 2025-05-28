package com.ince.gigalike.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ince.gigalike.constant.RedisLuaScriptConstant;
import com.ince.gigalike.enums.LuaStatusEnum;
import com.ince.gigalike.enums.NotificationTypeEnum;
import com.ince.gigalike.enums.RelatedTypeEnum;
import com.ince.gigalike.listener.notification.msg.NotificationEvent;
import com.ince.gigalike.listener.thumb.msg.ThumbEvent;
import com.ince.gigalike.mapper.ThumbMapper;
import com.ince.gigalike.model.dto.DoThumbRequest;
import com.ince.gigalike.model.entity.Blog;
import com.ince.gigalike.model.entity.Thumb;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.BlogService;
import com.ince.gigalike.service.ThumbService;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.utils.RedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author inceCheng
 * @description 针对表【thumb】的数据库操作Service实现
 * @createDate 2025-05-14 13:55:04
 */
@Service("thumbService")
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceMQImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final UserService userService;
    private final BlogService blogService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PulsarTemplate<ThumbEvent> pulsarTemplate;
    private final PulsarTemplate<NotificationEvent> notificationPulsarTemplate;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) throws PulsarClientException {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        Long blogId = doThumbRequest.getBlogId();
        String userThumbKey = RedisKeyUtil.getUserThumbKey(loginUserId);
        
        // 执行 Lua 脚本，点赞存入 Redis
        long result = redisTemplate.execute(
                RedisLuaScriptConstant.THUMB_SCRIPT_MQ,
                Arrays.asList(userThumbKey),
                blogId
        );
        if (LuaStatusEnum.FAIL.getValue() == result) {
            throw new RuntimeException("用户已点赞");
        }

        // 发送点赞事件
        ThumbEvent thumbEvent = ThumbEvent.builder()
                .blogId(blogId)
                .userId(loginUserId)
                .type(ThumbEvent.EventType.INCR)
                .eventTime(LocalDateTime.now())
                .build();
        pulsarTemplate.sendAsync("thumb-topic", thumbEvent).exceptionally(ex -> {
            redisTemplate.opsForHash().delete(userThumbKey, blogId.toString(), true);
            log.error("点赞事件发送失败: userId={}, blogId={}", loginUserId, blogId, ex);
            return null;
        });

        // 发送通知事件
        sendLikeNotification(loginUserId, blogId);
        
        return true;
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) throws PulsarClientException {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        Long blogId = doThumbRequest.getBlogId();
        String userThumbKey = RedisKeyUtil.getUserThumbKey(loginUserId);
        
        // 执行 Lua 脚本，点赞记录从 Redis 删除
        long result = redisTemplate.execute(
                RedisLuaScriptConstant.UNTHUMB_SCRIPT_MQ,
                Arrays.asList(userThumbKey),
                blogId
        );
        if (LuaStatusEnum.FAIL.getValue() == result) {
            throw new RuntimeException("用户未点赞");
        }
        
        ThumbEvent thumbEvent = ThumbEvent.builder()
                .blogId(blogId)
                .userId(loginUserId)
                .type(ThumbEvent.EventType.DECR)
                .eventTime(LocalDateTime.now())
                .build();
        pulsarTemplate.sendAsync("thumb-topic", thumbEvent).exceptionally(ex -> {
            redisTemplate.opsForHash().put(userThumbKey, blogId.toString(), true);
            log.error("取消点赞事件发送失败: userId={}, blogId={}", loginUserId, blogId, ex);
            return null;
        });
        
        return true;
    }

    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
        return redisTemplate.opsForHash().hasKey(RedisKeyUtil.getUserThumbKey(userId), blogId.toString());
    }

    /**
     * 发送点赞通知
     */
    private void sendLikeNotification(Long likerId, Long blogId) {
        try {
            // 获取博客信息
            Blog blog = blogService.getById(blogId);
            if (blog == null) {
                log.warn("博客不存在，无法发送点赞通知：blogId={}", blogId);
                return;
            }
            
            // 获取博客作者ID
            Long authorId = blog.getUserid();
            
            // 不给自己发通知
            if (likerId.equals(authorId)) {
                return;
            }
            
            // 准备额外数据
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("blogTitle", blog.getTitle());
            extraData.put("blogId", blogId);
            
            // 创建通知事件
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .userId(authorId)
                    .senderId(likerId)
                    .type(NotificationTypeEnum.LIKE.getCode())
                    .relatedId(blogId)
                    .relatedType(RelatedTypeEnum.BLOG.getCode())
                    .extraData(extraData)
                    .eventTime(LocalDateTime.now())
                    .build();
            
            // 发送通知事件
            notificationPulsarTemplate.sendAsync("notification-topic", notificationEvent)
                    .exceptionally(ex -> {
                        log.error("发送点赞通知失败: likerId={}, blogId={}, authorId={}", 
                                likerId, blogId, authorId, ex);
                        return null;
                    });
            
            log.debug("发送点赞通知成功: likerId={}, blogId={}, authorId={}", 
                    likerId, blogId, authorId);
                    
        } catch (Exception e) {
            log.error("发送点赞通知异常: likerId={}, blogId={}", likerId, blogId, e);
        }
    }
}





