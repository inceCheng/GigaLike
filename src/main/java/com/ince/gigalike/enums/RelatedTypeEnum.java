package com.ince.gigalike.enums;

import lombok.Getter;

/**
 * 关联资源类型枚举
 */
@Getter
public enum RelatedTypeEnum {
    
    /**
     * 博客
     */
    BLOG("BLOG", "博客"),
    
    /**
     * 评论
     */
    COMMENT("COMMENT", "评论"),
    
    /**
     * 用户
     */
    USER("USER", "用户");

    private final String code;
    private final String description;

    RelatedTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static RelatedTypeEnum getByCode(String code) {
        for (RelatedTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 