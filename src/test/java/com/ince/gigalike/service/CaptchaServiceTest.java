package com.ince.gigalike.service;

import com.ince.gigalike.model.dto.CaptchaResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
public class CaptchaServiceTest {

    @Resource
    private CaptchaService captchaService;

    @Test
    public void testGenerateCaptcha() {
        // 测试生成验证码
        CaptchaResponse response = captchaService.generateCaptcha();
        
        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getImage());
        assertTrue(response.getImage().startsWith("data:image/png;base64,"));
        assertEquals(32, response.getId().length()); // UUID去掉横线后的长度
    }

    @Test
    public void testVerifyCaptcha() {
        // 由于验证码是随机生成的，这里主要测试验证逻辑
        
        // 测试空参数
        assertFalse(captchaService.verifyCaptcha(null, "1234"));
        assertFalse(captchaService.verifyCaptcha("test-id", null));
        assertFalse(captchaService.verifyCaptcha("", "1234"));
        assertFalse(captchaService.verifyCaptcha("test-id", ""));
        
        // 测试不存在的验证码ID
        assertFalse(captchaService.verifyCaptcha("non-existent-id", "1234"));
    }

    @Test
    public void testCaptchaWorkflow() {
        // 测试完整的验证码工作流程
        
        // 1. 生成验证码
        CaptchaResponse response = captchaService.generateCaptcha();
        assertNotNull(response);
        
        String captchaId = response.getId();
        
        // 2. 验证错误的验证码
        assertFalse(captchaService.verifyCaptcha(captchaId, "WRONG"));
        
        // 注意：由于验证码是随机生成的，我们无法直接测试正确的验证码
        // 在实际应用中，可以通过Mock或者测试专用的验证码服务来测试
    }
} 