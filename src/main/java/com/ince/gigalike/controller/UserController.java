package com.ince.gigalike.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.constant.UserConstant;
import com.ince.gigalike.model.dto.UserLoginRequest;
import com.ince.gigalike.model.dto.UserRegisterRequest;
import com.ince.gigalike.model.dto.UserUpdatePasswordRequest;
import com.ince.gigalike.model.dto.UserUpdateRequest;
import com.ince.gigalike.model.entity.Blog;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.model.vo.UserVO;
import com.ince.gigalike.service.BlogService;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.utils.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private BlogService blogService;

    @PostMapping("/register")
    public BaseResponse<User> userRegister(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        User user = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(user);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody @Valid UserLoginRequest userLoginRequest, HttpServletRequest request) {
        User user = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(user);
    }

    @GetMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. 获取当前session
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 2. 清除session中的用户信息
            session.removeAttribute(UserConstant.LOGIN_USER);
            // 3. 使session立即失效
            session.invalidate();
        }
        
        // 4. 清除浏览器端的Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("GiGaSSIONID".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 立即过期
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        
        return ResultUtils.success("退出登录成功");
    }

    /**
     * 获取当前登录用户信息（脱敏）
     */
    @GetMapping("/current")
    @AuthCheck(mustLogin = true)
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(loginUser, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取当前用户发布的博客
     */
    @GetMapping("/blogs")
    @AuthCheck(mustLogin = true)
    public BaseResponse<Page<Blog>> getCurrentUserBlogs(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 创建分页查询条件
        Page<Blog> page = new Page<>(current, pageSize);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.orderByDesc("createTime");
        
        // 执行分页查询
        Page<Blog> blogPage = blogService.page(page, queryWrapper);
        return ResultUtils.success(blogPage);
    }

    @PostMapping("/update/password")
    @AuthCheck(mustLogin = true)
    public BaseResponse<User> updatePassword(@RequestBody @Valid UserUpdatePasswordRequest updatePasswordRequest,
                                           HttpServletRequest request) {
        User user = userService.updatePassword(updatePasswordRequest, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/update")
    @AuthCheck(mustLogin = true)
    public BaseResponse<User> updateUserInfo(@RequestBody @Valid UserUpdateRequest updateRequest,
                                           HttpServletRequest request) {
        User user = userService.updateUserInfo(updateRequest, request);
        return ResultUtils.success(user);
    }
}
