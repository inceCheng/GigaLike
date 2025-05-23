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
import com.ince.gigalike.mapper.UserMapper;
import com.ince.gigalike.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author inceCheng
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2025-05-14 13:57:31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();

        // 1. 校验参数
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 2. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        // 3. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", encryptPassword);
        User user = this.getOne(queryWrapper);

        // 4. 用户不存在或密码错误
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }


        // 5. 获取IP地址和归属地
        String ipAddress = IpUtils.getIpAddress(request);
        String ipLocation = IpUtils.getIpLocation(ipAddress);

        // 6. 更新用户信息
        user.setLastLoginAt(new Date());
        user.setLastLoginIp(ipAddress);
        user.setLastLoginIpLocation(ipLocation);

        // 7. 记录用户的登录状态
        request.getSession().setAttribute(UserConstant.LOGIN_USER, user);

        this.updateById(user);
        return user;
    }

    @Override
    public User userRegister(UserRegisterRequest userRegisterRequest) {
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();

        // 1. 校验参数
        if (StringUtils.isAnyBlank(username, password, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (username.length() < 4 || username.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度必须在4-20个字符之间");
        }
        if (password.length() < 5 || password.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度必须在5-20个字符之间");
        }
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 2. 校验用户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }

        // 3. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        // 4. 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword);
        user.setStatus("ACTIVE");
        user.setRole("USER");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 5. 保存用户
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
}




