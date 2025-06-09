package com.ince.gigalike.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ince.gigalike.constant.UserConstant;
import com.ince.gigalike.enums.ErrorCode;
import com.ince.gigalike.exception.BusinessException;
import com.ince.gigalike.model.dto.UserLoginRequest;
import com.ince.gigalike.model.dto.UserRegisterRequest;
import com.ince.gigalike.model.dto.UserUpdatePasswordRequest;
import com.ince.gigalike.model.dto.UserUpdateRequest;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.service.CaptchaService;
import com.ince.gigalike.service.EmailService;
import com.ince.gigalike.mapper.UserMapper;
import com.ince.gigalike.utils.IpUtils;
import com.ince.gigalike.utils.DeviceUtils;
import com.ince.gigalike.enums.NotificationTypeEnum;
import com.ince.gigalike.listener.notification.msg.NotificationEvent;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.pulsar.core.PulsarTemplate;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author inceCheng
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2025-05-14 13:57:31
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    
    @Resource
    private CaptchaService captchaService;
    
    @Resource
    private EmailService emailService;
    
    @Resource
    private PulsarTemplate<NotificationEvent> notificationPulsarTemplate;

    @Override
    public User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();
        String captchaCode = userLoginRequest.getCaptchaCode();
        String captchaId = userLoginRequest.getCaptchaId();

        // 1. 校验参数
        if (StringUtils.isAnyBlank(username, password, captchaCode, captchaId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        // 2. 验证验证码
        boolean captchaValid = captchaService.verifyCaptcha(captchaId, captchaCode);
        if (!captchaValid) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR);
        }

        // 3. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        // 4. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", encryptPassword);
        User user = this.getOne(queryWrapper);

        // 5. 用户不存在或密码错误
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        // 6. 获取IP地址和归属地
        String ipAddress = IpUtils.getIpAddress(request);
        String ipLocation = IpUtils.getIpLocation(ipAddress);

        // 7. 更新用户信息
        user.setLastLoginAt(new Date());
        user.setLastLoginIp(ipAddress);
        user.setLastLoginIpLocation(ipLocation);

        // 8. 记录用户的登录状态
        request.getSession().setAttribute(UserConstant.LOGIN_USER, user);

        this.updateById(user);
        
        // 9. 发送登录成功通知
        sendLoginSuccessNotification(user, request);
        
        return user;
    }

    @Override
    public User userRegister(UserRegisterRequest userRegisterRequest) {
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        String email = userRegisterRequest.getEmail();
        String emailCode = userRegisterRequest.getEmailCode();
        String captchaCode = userRegisterRequest.getCaptchaCode();
        String captchaId = userRegisterRequest.getCaptchaId();

        // 1. 校验参数
        if (StringUtils.isAnyBlank(username, password, confirmPassword, email, emailCode, captchaCode, captchaId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        // 2. 验证图形验证码
        boolean captchaValid = captchaService.verifyCaptcha(captchaId, captchaCode);
        if (!captchaValid) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "图形验证码错误");
        }
        
        // 3. 验证邮箱验证码
        boolean emailCodeValid = emailService.verifyEmailCode(email, emailCode);
        if (!emailCodeValid) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "邮箱验证码错误");
        }
        // 4. 校验其他参数
        if (username.length() < 4 || username.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度必须在4-20个字符之间");
        }
        if (password.length() < 5 || password.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度必须在5-20个字符之间");
        }
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 5. 校验用户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }
        
        // 6. 校验邮箱是否已被使用
        QueryWrapper<User> emailQueryWrapper = new QueryWrapper<>();
        emailQueryWrapper.eq("email", email);
        long emailCount = this.count(emailQueryWrapper);
        if (emailCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已被注册");
        }

        // 7. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        // 8. 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword);
        user.setEmail(email);
        user.setStatus("ACTIVE");
        user.setRole("USER");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 9. 保存用户
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }

        return user;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.LOGIN_USER);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return (User) userObj;
    }

    @Override
    public User updatePassword(UserUpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        // 1. 获取当前登录用户
        User loginUser = this.getLoginUser(request);
        
        // 2. 校验参数
        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();
        String confirmPassword = updatePasswordRequest.getConfirmPassword();
        
        if (StringUtils.isAnyBlank(oldPassword, newPassword, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        // 3. 校验新密码格式
        if (newPassword.length() < 5 || newPassword.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码长度必须在5-20个字符之间");
        }
        
        // 4. 校验新密码和确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的新密码不一致");
        }
        
        // 5. 校验原密码是否正确
        String encryptOldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes(StandardCharsets.UTF_8));
        if (!encryptOldPassword.equals(loginUser.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码错误");
        }
        
        // 6. 更新密码
        String encryptNewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8));
        loginUser.setPassword(encryptNewPassword);
        loginUser.setUpdateTime(new Date());
        
        boolean updateResult = this.updateById(loginUser);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改密码失败");
        }
        
        return loginUser;
    }

    @Override
    public User updateUserInfo(UserUpdateRequest updateRequest, HttpServletRequest request) {
        // 1. 获取当前登录用户
        User loginUser = this.getLoginUser(request);
        
        // 2. 校验邮箱是否已被其他用户使用
        String newEmail = updateRequest.getEmail();
        if (StringUtils.isNotBlank(newEmail)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", newEmail);
            queryWrapper.ne("id", loginUser.getId());
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已被其他用户使用");
            }
        }
        
        // 3. 更新用户信息
        BeanUtils.copyProperties(updateRequest, loginUser);
        loginUser.setUpdateTime(new Date());
        
        boolean updateResult = this.updateById(loginUser);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户信息失败");
        }
        
        return loginUser;
    }
    
    /**
     * 发送登录成功通知
     */
    private void sendLoginSuccessNotification(User user, HttpServletRequest request) {
        try {
            // 获取登录时间（格式化）
            LocalDateTime loginTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String loginTimeStr = loginTime.format(formatter);
            
            // 获取登录地点
            String loginLocation = user.getLastLoginIpLocation();
            
            // 获取设备信息
            String deviceInfo = DeviceUtils.getDeviceInfo(request);
            
            // 构建通知内容
            String displayName = StringUtils.isNotBlank(user.getDisplayName()) ? user.getDisplayName() : user.getUsername();
            String notificationContent = String.format(
                "欢迎回来，%s！您已成功登录 GigaLike 🎉\n登录时间：%s\n登录地点：%s\n设备信息：%s",
                displayName, loginTimeStr, loginLocation, deviceInfo
            );
            
            // 准备额外数据
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("content", notificationContent);
            extraData.put("loginTime", loginTimeStr);
            extraData.put("loginLocation", loginLocation);
            extraData.put("deviceInfo", deviceInfo);
            
            // 创建通知事件
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .userId(user.getId())
                    .senderId(null) // 系统通知，没有发送者
                    .type(NotificationTypeEnum.SYSTEM.getCode())
                    .relatedId(null) // 系统通知不关联具体资源
                    .relatedType(null)
                    .extraData(extraData)
                    .eventTime(loginTime)
                    .build();
            
            // 发送通知事件
            notificationPulsarTemplate.sendAsync("notification-topic", notificationEvent)
                    .exceptionally(ex -> {
                        log.error("发送登录成功通知失败: userId={}, username={}", 
                                user.getId(), user.getUsername(), ex);
                        return null;
                    });
            
            log.debug("发送登录成功通知: userId={}, username={}, location={}, device={}", 
                    user.getId(), user.getUsername(), loginLocation, deviceInfo);
                    
        } catch (Exception e) {
            log.error("发送登录成功通知异常: userId={}, username={}", 
                    user.getId(), user.getUsername(), e);
        }
    }
}




