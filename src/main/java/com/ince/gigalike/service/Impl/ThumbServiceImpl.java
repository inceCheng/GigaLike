package com.ince.gigalike.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ince.gigalike.enums.ErrorCode;
import com.ince.gigalike.exception.BusinessException;
import com.ince.gigalike.model.dto.DoThumbRequest;
import com.ince.gigalike.model.entity.Blog;
import com.ince.gigalike.model.entity.Thumb;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.BlogService;
import com.ince.gigalike.service.ThumbService;
import com.ince.gigalike.mapper.ThumbMapper;
import com.ince.gigalike.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author inceCheng
 * @description 针对表【thumb】的数据库操作Service实现
 * @createDate 2025-05-14 13:55:04
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final UserService userService;

    private final BlogService blogService;

    private final TransactionTemplate transactionTemplate;

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
                boolean exists = this.lambdaQuery()
                        .eq(Thumb::getUserId, loginUser.getId())
                        .eq(Thumb::getBlogId, blogId)
                        .exists();
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
                return update && this.save(thumb);
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
                Thumb thumb = this.lambdaQuery()
                        .eq(Thumb::getUserId, loginUser.getId())
                        .eq(Thumb::getBlogId, blogId)
                        .one();
                if (thumb == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有点过赞");
                }
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount - 1")
                        .update();

                return update && this.removeById(thumb.getId());
            });
        }
    }
}




