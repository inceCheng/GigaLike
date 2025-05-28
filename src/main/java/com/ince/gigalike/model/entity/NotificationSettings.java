package com.ince.gigalike.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 通知设置实体类
 * @TableName notification_settings
 */
@Data
@TableName(value = "notification_settings")
public class NotificationSettings {
    
    /**
     * 设置唯一标识符
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 点赞通知开关
     */
    @TableField("like_enabled")
    private Integer likeEnabled;

    /**
     * 评论通知开关
     */
    @TableField("comment_enabled")
    private Integer commentEnabled;

    /**
     * 关注通知开关
     */
    @TableField("follow_enabled")
    private Integer followEnabled;

    /**
     * 系统通知开关
     */
    @TableField("system_enabled")
    private Integer systemEnabled;

    /**
     * 邮件通知开关
     */
    @TableField("email_enabled")
    private Integer emailEnabled;

    /**
     * 推送通知开关
     */
    @TableField("push_enabled")
    private Integer pushEnabled;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
} 