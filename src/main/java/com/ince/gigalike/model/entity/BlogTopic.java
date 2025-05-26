package com.ince.gigalike.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 博客话题关联实体类
 * @TableName blog_topics
 */
@Data
@TableName(value = "blog_topics")
public class BlogTopic {
    /**
     * 关联记录唯一标识符
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 博客ID，关联blog表
     */
    @TableField("blog_id")
    private Long blogId;

    /**
     * 话题ID，关联topics表
     */
    @TableField("topic_id")
    private Long topicId;

    /**
     * 关联创建时间
     */
    @TableField("create_time")
    private Date createTime;
} 