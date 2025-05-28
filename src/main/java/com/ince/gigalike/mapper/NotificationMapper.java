package com.ince.gigalike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ince.gigalike.model.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 通知Mapper接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 分页查询用户通知（包含发送者信息）
     */
    IPage<Notification> selectNotificationPageWithSender(
            Page<Notification> page,
            @Param("userId") Long userId,
            @Param("isRead") Integer isRead,
            @Param("type") String type
    );
    
    /**
     * 批量标记为已读
     */
    @Update("UPDATE notifications SET is_read = 1, read_time = NOW() WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") Long userId);
    
    /**
     * 获取用户未读通知数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * 删除用户的旧通知（保留最近的指定数量）
     */
    int deleteOldNotifications(@Param("userId") Long userId, @Param("keepCount") int keepCount);
} 