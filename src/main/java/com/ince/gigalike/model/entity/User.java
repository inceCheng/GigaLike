package com.ince.gigalike.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @TableName users
 */
@Data
@TableName(value = "users")
public class User {
    /**
     * 用户唯一标识符
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名，用于登录和显示
     */
    private String username;

    /**
     * 加密后的用户密码
     */
    private String password;

    /**
     * 用户邮箱，用于登录和通知
     */
    private String email;

    /**
     * 用户显示名称，用于界面展示
     */
    @TableField("display_name")
    private String displayName;

    /**
     * 用户头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 用户个人简介
     */
    private String bio;

    /**
     * 用户状态：活跃/不活跃/被封禁/已删除
     */
    private Object status;

    /**
     * 邮箱是否已验证
     */
    @TableField("email_verified")
    private Integer emailVerified;

    /**
     * 用户角色：普通用户/编辑/管理员
     */
    private Object role;

    /**
     * 用户语言区域设置
     */
    private String locale;

    /**
     * 用户时区设置
     */
    private String timezone;

    /**
     * 用户创建时间
     */
    @TableField("createTime")
    private Date createTime;

    /**
     * 用户信息最后更新时间
     */
    @TableField("updateTime")
    private Date updateTime;

    /**
     * 用户最后登录时间
     */
    @TableField("last_login_at")
    private Date lastLoginAt;

    /**
     * 第三方登录提供商名称
     */
    @TableField("social_provider")
    private String socialProvider;

    /**
     * 第三方登录提供商中的用户ID
     */
    @TableField("social_id")
    private String socialId;

    /**
     * 用户额外元数据，存储自定义信息
     */
    private Object metadata;

}