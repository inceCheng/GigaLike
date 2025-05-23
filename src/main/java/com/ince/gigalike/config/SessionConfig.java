package com.ince.gigalike.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800, redisNamespace = "spring:session") // 30分钟过期
public class SessionConfig {
    
    @Value("${spring.session.timeout}")
    private int sessionTimeout;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("GiGaSSIONID"); // cookie名称
        // 将timeout从秒转换为秒
        serializer.setCookieMaxAge(sessionTimeout);
        serializer.setCookiePath("/");
        serializer.setUseHttpOnlyCookie(true); // 防止XSS攻击
        serializer.setSameSite("Lax"); // 防止CSRF攻击
        serializer.setUseSecureCookie(false); // 本地开发时设为false，生产环境应该设为true
        return serializer;
    }
} 