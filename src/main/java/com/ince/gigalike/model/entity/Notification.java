package com.ince.gigalike.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 通知实体类
 * @TableName notifications
 */
@Data
@TableName(value = "notifications", autoResultMap = true)
public class Notification {
    
    /**
     * 通知唯一标识符
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 接收通知的用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 发送通知的用户ID（系统通知时为NULL）
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 通知类型：LIKE, COMMENT, FOLLOW, SYSTEM
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 关联的资源ID（如博客ID、评论ID等）
     */
    @TableField("related_id")
    private Long relatedId;

    /**
     * 关联资源类型：BLOG, COMMENT, USER
     */
    @TableField("related_type")
    private String relatedType;

    /**
     * 是否已读：0-未读，1-已读
     */
    @TableField("is_read")
    private Integer isRead;

    /**
     * 阅读时间
     */
    @TableField("read_time")
    private Date readTime;

    /**
     * 额外数据（如博客标题、用户头像等）
     */
    @TableField(value = "extra_data", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extraData;

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