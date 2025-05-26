package com.ince.gigalike.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 话题实体类
 * @TableName topics
 */
@Data
@TableName(value = "topics")
public class Topic {
    /**
     * 话题唯一标识符
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 话题名称，如"前端开发"、"Java学习"
     */
    private String name;

    /**
     * 话题描述
     */
    private String description;

    /**
     * 话题封面图片URL
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 话题主题色，十六进制颜色值
     */
    private String color;

    /**
     * 话题状态：active/inactive/banned
     */
    private String status;

    /**
     * 使用该话题的帖子数量
     */
    @TableField("post_count")
    private Integer postCount;

    /**
     * 关注该话题的用户数量
     */
    @TableField("follow_count")
    private Integer followCount;

    /**
     * 话题创建者ID，关联users表
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 是否为官方话题
     */
    @TableField("is_official")
    private Boolean isOfficial;

    /**
     * 话题创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 话题更新时间
     */
    @TableField("update_time")
    private Date updateTime;
} 