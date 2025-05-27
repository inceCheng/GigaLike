package com.ince.gigalike.model.dto;

import lombok.Data;

@Data
public class CaptchaResponse {
    /**
     * 验证码唯一标识
     */
    private String id;
    
    /**
     * Base64编码的验证码图片
     */
    private String image;
    
    public CaptchaResponse(String id, String image) {
        this.id = id;
        this.image = image;
    }
} 