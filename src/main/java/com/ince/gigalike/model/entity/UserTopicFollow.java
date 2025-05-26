package com.ince.gigalike.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户话题关注实体类
 * @TableName user_topic_follows
 */
@Data
@TableName(value = "user_topic_follows")
public class UserTopicFollow {
    /**
     * 关注记录唯一标识符
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID，关联users表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 话题ID，关联topics表
     */
    @TableField("topic_id")
    private Long topicId;

    /**
     * 关注时间
     */
    @TableField("create_time")
    private Date createTime;
} 