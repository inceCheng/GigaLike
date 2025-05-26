package com.ince.gigalike.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 * 用于标记需要登录验证的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    
    /**
     * 是否必须登录，默认为true
     */
    boolean mustLogin() default true;
    
    /**
     * 可选的角色要求
     */
    String[] roles() default {};
} 