package com.ince.gigalike.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ince.gigalike.constant.UserConstant;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
* @author inceCheng
* @description 针对表【users】的数据库操作Service实现
* @createDate 2025-05-14 13:57:31
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(UserConstant.LOGIN_USER);
    }

}




