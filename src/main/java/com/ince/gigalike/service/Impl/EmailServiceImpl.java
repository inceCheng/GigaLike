package com.ince.gigalike.service.Impl;

import com.ince.gigalike.service.EmailService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    
    @Resource
    private JavaMailSender javaMailSender;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 邮箱验证码在Redis中的key前缀
     */
    private static final String EMAIL_CODE_KEY_PREFIX = "email_code:";
    
    /**
     * 邮箱验证码发送频率限制key前缀
     */
    private static final String EMAIL_SEND_LIMIT_KEY_PREFIX = "email_send_limit:";
    
    /**
     * 邮箱验证码过期时间（分钟）
     */
    private static final long EMAIL_CODE_EXPIRE_MINUTES = 10;
    
    /**
     * 邮箱验证码发送间隔（秒）
     */
    private static final long EMAIL_SEND_INTERVAL_SECONDS = 60;
    
    @Override
    public boolean sendEmailCode(String email) {
        try {
            // 1. 检查发送频率限制
            String limitKey = EMAIL_SEND_LIMIT_KEY_PREFIX + email;
            if (stringRedisTemplate.hasKey(limitKey)) {
                log.warn("邮箱验证码发送过于频繁，邮箱: {}", email);
                return false;
            }
            
            // 2. 生成6位数字验证码
            String emailCode = generateEmailCode();
            
            // 3. 发送邮件
            boolean sendResult = sendEmail(email, emailCode);
            if (!sendResult) {
                return false;
            }
            
            // 4. 存储验证码到Redis
            String codeKey = EMAIL_CODE_KEY_PREFIX + email;
            stringRedisTemplate.opsForValue().set(codeKey, emailCode, 
                    EMAIL_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            // 5. 设置发送频率限制
            stringRedisTemplate.opsForValue().set(limitKey, "1", 
                    EMAIL_SEND_INTERVAL_SECONDS, TimeUnit.SECONDS);
            
            log.info("邮箱验证码发送成功，邮箱: {}", email);
            return true;
            
        } catch (Exception e) {
            log.error("发送邮箱验证码失败，邮箱: {}", email, e);
            return false;
        }
    }
    
    @Override
    public boolean verifyEmailCode(String email, String emailCode) {
        // 1. 参数校验
        if (StringUtils.isAnyBlank(email, emailCode)) {
            return false;
        }
        
        // 2. 从Redis获取验证码
        String codeKey = EMAIL_CODE_KEY_PREFIX + email;
        String storedCode = stringRedisTemplate.opsForValue().get(codeKey);
        
        // 3. 验证码不存在或已过期
        if (StringUtils.isBlank(storedCode)) {
            log.warn("邮箱验证码不存在或已过期，邮箱: {}", email);
            return false;
        }
        
        // 4. 验证成功后立即删除验证码（防止重复使用）
        stringRedisTemplate.delete(codeKey);
        
        // 5. 比较验证码
        boolean result = storedCode.equals(emailCode.trim());
        log.info("邮箱验证码验证结果: {}, 邮箱: {}", result, email);
        return result;
    }
    
    /**
     * 生成6位数字验证码
     */
    private String generateEmailCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 发送邮件
     */
    private boolean sendEmail(String toEmail, String emailCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("GigaLike - 邮箱验证码");
            message.setText(buildEmailContent(emailCode));
            
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败，收件人: {}", toEmail, e);
            return false;
        }
    }
    
    /**
     * 构建邮件内容
     */
    private String buildEmailContent(String emailCode) {
        return String.format(
            "亲爱的用户：\n\n" +
            "您正在注册GigaLike账号，您的邮箱验证码是：%s\n\n" +
            "验证码有效期为10分钟，请及时使用。\n" +
            "如果这不是您的操作，请忽略此邮件。\n\n" +
            "GigaLike团队\n" +
            "%s",
            emailCode,
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
} 