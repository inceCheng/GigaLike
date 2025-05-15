package com.ince.gigalike.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ince.gigalike.constant.ThumbConstant;
import com.ince.gigalike.enums.ErrorCode;
import com.ince.gigalike.exception.BusinessException;
import com.ince.gigalike.manage.cache.CacheManager;
import com.ince.gigalike.model.dto.DoThumbRequest;
import com.ince.gigalike.model.entity.Blog;
import com.ince.gigalike.model.entity.Thumb;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.BlogService;
import com.ince.gigalike.service.ThumbService;
import com.ince.gigalike.mapper.ThumbMapper;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.utils.RedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author inceCheng
 * @description 针对表【thumb】的数据库操作Service实现
 * @createDate 2025-05-14 13:55:04
 */
@Service("thumbServiceLocalCache")
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final UserService userService;

    private final BlogService blogService;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final CacheManager cacheManager;

    /**
     * 点赞
     *
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 加锁 保证同一个用户只能点赞一次
        synchronized (loginUser.getId().toString().intern()) {
            // 编程式事务
            return transactionTemplate.execute(status -> {
                Long blogId = doThumbRequest.getBlogId();
                // 判断是否点过赞(Redis)
                Boolean exists = this.hasThumb(blogId, loginUser.getId());
                if (exists) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经点过赞了");
                }
                // 点赞数加一
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount + 1")
                        .update();

                Thumb thumb = new Thumb();
                thumb.setUserId(loginUser.getId());
                thumb.setBlogId(blogId);
                // 更新成功才执行
                boolean success = update && this.save(thumb);
                // 点赞记录存入 Redis
                if (success) {
                    String hashKey = ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId();
                    String fieldKey = blogId.toString();
                    Long realThumbId = thumb.getId();
                    redisTemplate.opsForHash().put(hashKey, fieldKey, realThumbId);
                    cacheManager.putIfPresent(hashKey, fieldKey, realThumbId);
                }
                return success;
            });
        }
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 加锁
        synchronized (loginUser.getId().toString().intern()) {

            // 编程式事务
            return transactionTemplate.execute(status -> {
                Long blogId = doThumbRequest.getBlogId();
                // 判断是否点过赞(Redis)
                Object thumbIdObj = cacheManager.get(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId(), blogId.toString());
                if (thumbIdObj == null || thumbIdObj.equals(ThumbConstant.UN_THUMB_CONSTANT)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "还没有点赞呢");
                }
                // 点过赞，则获取到点赞记录id
                Long thumbId = Long.parseLong(thumbIdObj.toString());
                // 点赞数减一
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount - 1")
                        .update();
                boolean success = update && this.removeById(thumbId);
                // 点赞记录从 Redis 删除
                if (success) {
                    String hashKey = ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId();
                    String fieldKey = blogId.toString();
                    redisTemplate.opsForHash().delete(hashKey, fieldKey);
                    cacheManager.putIfPresent(hashKey, fieldKey, ThumbConstant.UN_THUMB_CONSTANT);
                }
                return success;
            });
        }
    }


    /**
     * 是否点赞
     *
     * @param blogId 博客id
     * @param userId 用户id
     * @return
     */
    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
        Object thumbIdObj = cacheManager.get(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, blogId.toString());
        if (thumbIdObj == null) {
            return false;
        }
        // 更安全的类型转换
        Long thumbId;
        if (thumbIdObj instanceof Long) {
            thumbId = (Long) thumbIdObj;
        } else if (thumbIdObj instanceof Number) { // 包括 Integer, Short, etc.
            thumbId = ((Number) thumbIdObj).longValue();
        } else {
            // 如果类型未知或不符合预期，可以记录日志或抛出异常
            log.warn("Unexpected type for thumbIdObj in cache: {} for userId: {}, blogId: {}", thumbIdObj.getClass(), userId, blogId);
            return false; // 或者根据业务逻辑决定如何处理
        }
        return !thumbId.equals(ThumbConstant.UN_THUMB_CONSTANT);
    }

}




