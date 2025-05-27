package com.ince.gigalike.service.Impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.ince.gigalike.model.dto.CaptchaResponse;
import com.ince.gigalike.service.CaptchaService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    
    @Resource
    private DefaultKaptcha defaultKaptcha;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 验证码在Redis中的key前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    
    /**
     * 验证码过期时间（分钟）
     */
    private static final long CAPTCHA_EXPIRE_MINUTES = 5;
    
    @Override
    public CaptchaResponse generateCaptcha() {
        // 1. 生成验证码文本
        String captchaText = defaultKaptcha.createText();
        
        // 2. 生成验证码图片
        BufferedImage captchaImage = defaultKaptcha.createImage(captchaText);
        
        // 3. 将图片转换为Base64
        String base64Image = convertImageToBase64(captchaImage);
        
        // 4. 生成唯一ID
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        
        // 5. 将验证码文本存储到Redis，设置过期时间
        String redisKey = CAPTCHA_KEY_PREFIX + captchaId;
        stringRedisTemplate.opsForValue().set(redisKey, captchaText.toUpperCase(), 
                CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        // 6. 返回响应对象
        return new CaptchaResponse(captchaId, "data:image/png;base64," + base64Image);
    }
    
    @Override
    public boolean verifyCaptcha(String captchaId, String captchaCode) {
        // 1. 参数校验
        if (StringUtils.isAnyBlank(captchaId, captchaCode)) {
            return false;
        }
        
        // 2. 从Redis获取验证码
        String redisKey = CAPTCHA_KEY_PREFIX + captchaId;
        String storedCaptcha = stringRedisTemplate.opsForValue().get(redisKey);
        
        // 3. 验证码不存在或已过期
        if (StringUtils.isBlank(storedCaptcha)) {
            return false;
        }
        
        // 4. 验证成功后立即删除验证码（防止重复使用）
        stringRedisTemplate.delete(redisKey);
        
        // 5. 比较验证码（不区分大小写）
        return storedCaptcha.equalsIgnoreCase(captchaCode.trim());
    }
    
    /**
     * 将BufferedImage转换为Base64字符串
     */
    private String convertImageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("验证码图片转换失败", e);
        }
    }
} 