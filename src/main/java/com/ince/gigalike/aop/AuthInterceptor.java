package com.ince.gigalike.aop;

import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.constant.UserConstant;
import com.ince.gigalike.enums.ErrorCode;
import com.ince.gigalike.exception.BusinessException;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;

/**
 * 权限校验 AOP
 * 用于拦截带有 @AuthCheck 注解的方法，进行登录状态验证
 */
@Aspect
@Component
@Slf4j
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取当前请求
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        
        // 如果需要登录验证
        if (authCheck.mustLogin()) {
            // 当前登录用户
            User loginUser = null;
            try {
                loginUser = userService.getLoginUser(request);
            } catch (BusinessException e) {
                // 如果获取登录用户失败，说明未登录
                log.warn("用户未登录，访问路径: {}", request.getRequestURI());
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            
            // 检查用户状态
            if (loginUser == null) {
                log.warn("用户未登录，访问路径: {}", request.getRequestURI());
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            
            // 如果有角色要求，进行角色验证
            String[] requiredRoles = authCheck.roles();
            if (requiredRoles.length > 0) {
                String userRole = (String) loginUser.getRole();
                boolean hasRole = false;
                for (String requiredRole : requiredRoles) {
                    if (StringUtils.equals(userRole, requiredRole)) {
                        hasRole = true;
                        break;
                    }
                }
                if (!hasRole) {
                    log.warn("用户权限不足，用户角色: {}, 需要角色: {}, 访问路径: {}", 
                            userRole, String.join(",", requiredRoles), request.getRequestURI());
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
            
            log.info("用户 {} 通过权限验证，访问路径: {}", loginUser.getUsername(), request.getRequestURI());
        }
        
        // 通过权限校验，执行原方法
        return joinPoint.proceed();
    }
} 