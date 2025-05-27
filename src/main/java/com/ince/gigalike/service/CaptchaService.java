package com.ince.gigalike.service;

import com.ince.gigalike.model.dto.CaptchaResponse;

/**
 * 验证码服务接口
 */
public interface CaptchaService {
    
    /**
     * 生成验证码
     * @return 验证码响应对象，包含验证码ID和Base64图片
     */
    CaptchaResponse generateCaptcha();
    
    /**
     * 验证验证码
     * @param captchaId 验证码ID
     * @param captchaCode 用户输入的验证码
     * @return 验证是否成功
     */
    boolean verifyCaptcha(String captchaId, String captchaCode);
} 