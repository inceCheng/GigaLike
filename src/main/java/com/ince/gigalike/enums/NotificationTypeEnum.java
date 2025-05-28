package com.ince.gigalike.enums;

import lombok.Getter;

/**
 * 通知类型枚举
 */
@Getter
public enum NotificationTypeEnum {
    
    /**
     * 点赞通知
     */
    LIKE("LIKE", "点赞通知"),
    
    /**
     * 评论通知
     */
    COMMENT("COMMENT", "评论通知"),
    
    /**
     * 关注通知
     */
    FOLLOW("FOLLOW", "关注通知"),
    
    /**
     * 系统通知
     */
    SYSTEM("SYSTEM", "系统通知");

    private final String code;
    private final String description;

    NotificationTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static NotificationTypeEnum getByCode(String code) {
        for (NotificationTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 