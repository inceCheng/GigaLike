package com.ince.gigalike.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 话题视图对象VO
 */
@Data
public class TopicVO {

    /**
     * 话题唯一标识符
     */
    private Long id;

    /**
     * 话题名称
     */
    private String name;

    /**
     * 话题描述
     */
    private String description;

    /**
     * 话题封面图片URL
     */
    private String coverImage;

    /**
     * 话题主题色
     */
    private String color;

    /**
     * 话题状态
     */
    private String status;

    /**
     * 使用该话题的帖子数量
     */
    private Integer postCount;

    /**
     * 关注该话题的用户数量
     */
    private Integer followCount;

    /**
     * 是否为官方话题
     */
    private Boolean isOfficial;

    /**
     * 当前用户是否已关注该话题
     */
    private Boolean isFollowed;

    /**
     * 话题创建时间
     */
    private Date createTime;

    /**
     * 话题创建者信息
     */
    private UserVO creator;
} 