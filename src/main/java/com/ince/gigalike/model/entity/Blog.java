package com.ince.gigalike.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @TableName blog
 */
@Data
@TableName(value = "blog")
public class Blog {
    /**
     * 博客文章唯一标识符
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发布文章的用户ID，关联users表
     */
    private Long userid;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章封面图片URL
     */
    private String coverImg;

    /**
     * 文章正文内容，支持富文本
     */
    private String content;

    /**
     * 文章获得的点赞总数
     */
    private Integer thumbCount;

    /**
     * 文章创建时间
     */
    private Date createTime;

    /**
     * 文章最后更新时间
     */
    private Date updateTime;
}