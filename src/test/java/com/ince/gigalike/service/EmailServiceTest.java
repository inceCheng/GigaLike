package com.ince.gigalike.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379",
    "spring.mail.host=smtp.qq.com",
    "spring.mail.port=587",
    "spring.mail.username=test@qq.com",
    "spring.mail.password=testpassword"
})
public class EmailServiceTest {

    @Resource
    private EmailService emailService;

    @Test
    public void testVerifyEmailCode() {
        // 测试邮箱验证码验证逻辑
        
        // 测试空参数
        assertFalse(emailService.verifyEmailCode(null, "123456"));
        assertFalse(emailService.verifyEmailCode("test@example.com", null));
        assertFalse(emailService.verifyEmailCode("", "123456"));
        assertFalse(emailService.verifyEmailCode("test@example.com", ""));
        
        // 测试不存在的邮箱验证码
        assertFalse(emailService.verifyEmailCode("nonexistent@example.com", "123456"));
    }

    // 注意：发送邮件的测试需要真实的邮件配置，在CI/CD环境中可能需要Mock
    // @Test
    // public void testSendEmailCode() {
    //     // 需要配置真实的邮件服务器信息才能测试
    //     // boolean result = emailService.sendEmailCode("test@example.com");
    //     // assertTrue(result);
    // }
} 