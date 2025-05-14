package com.ince.gigalike.service;

import com.ince.gigalike.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author inceCheng
 * @description 针对表【users】的数据库操作Service
 * @createDate 2025-05-14 13:57:31
 */
public interface UserService extends IService<User> {

    public User getLoginUser(HttpServletRequest request);

}
