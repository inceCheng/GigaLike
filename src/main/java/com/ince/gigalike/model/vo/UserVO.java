package com.ince.gigalike.model.vo;

import lombok.Data;

@Data
public class UserVO {
    /**
     * 用户唯一标识符
     */
    private Long id;

    /**
     * 用户名，用于登录和显示
     */
    private String username;

    /**
     * 用户邮箱，用于登录和通知
     */
    private String email;

    /**
     * 用户显示名称，用于界面展示
     */
    private String displayName;

    /**
     * 用户头像URL
     */
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
     * IP归属地
     */
    private String lastLoginIpLocation;
} 