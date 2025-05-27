package com.ince.gigalike.service;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送邮箱验证码
     * @param email 邮箱地址
     * @return 是否发送成功
     */
    boolean sendEmailCode(String email);
    
    /**
     * 验证邮箱验证码
     * @param email 邮箱地址
     * @param emailCode 邮箱验证码
     * @return 验证是否成功
     */
    boolean verifyEmailCode(String email, String emailCode);
} 