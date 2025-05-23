package com.ince.gigalike.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionCookieInterceptor implements HandlerInterceptor {

    @Value("${spring.session.timeout}")
    private int sessionTimeout;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("GiGaSSIONID".equals(cookie.getName())) {
                    // 创建一个新的Cookie，使用相同的值但更新过期时间
                    Cookie newCookie = new Cookie("GiGaSSIONID", cookie.getValue());
                    newCookie.setMaxAge(sessionTimeout);
                    newCookie.setPath("/");
                    newCookie.setHttpOnly(true);
                    newCookie.setSecure(false); // 本地开发设置为false，生产环境设置为true
                    response.addCookie(newCookie);
                    break;
                }
            }
        }
        return true;
    }
} 