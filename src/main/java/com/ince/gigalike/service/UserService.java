package com.ince.gigalike.service;

import com.ince.gigalike.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ince.gigalike.model.dto.UserLoginRequest;
import com.ince.gigalike.model.dto.UserRegisterRequest;
import com.ince.gigalike.model.dto.UserUpdatePasswordRequest;
import com.ince.gigalike.model.dto.UserUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author inceCheng
 * @description 针对表【users】的数据库操作Service
 * @createDate 2025-05-14 13:57:31
 */
public interface UserService extends IService<User> {
    /**
     * 用户登录
     * @param userLoginRequest 登录请求
     * @param request HTTP请求
     * @return 登录成功的用户信息
     */
    User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 用户注册
     * @param userRegisterRequest 注册请求
     * @return 注册成功的用户信息
     */
    User userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 获取当前登录用户
     * @param request HTTP请求
     * @return 当前登录用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 修改密码
     * @param updatePasswordRequest 修改密码请求
     * @param request HTTP请求
     * @return 更新后的用户信息
     */
    User updatePassword(UserUpdatePasswordRequest updatePasswordRequest, HttpServletRequest request);

    /**
     * 更新用户信息
     * @param updateRequest 更新信息请求
     * @param request HTTP请求
     * @return 更新后的用户信息
     */
    User updateUserInfo(UserUpdateRequest updateRequest, HttpServletRequest request);
}
