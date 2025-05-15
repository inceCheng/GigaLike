package com.ince.gigalike.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /***
     * 配置 RedisTemplate，用于操作 Redis 数据库。
     * 配置后，Spring Boot 会自动使用该配置创建 RedisTemplate 对象，
     * 并将其注入到 Spring 容器中，方便在应用程序中使用。
     * 注意：在使用 RedisTemplate 时，需要在 pom.xml 中添加 spring-data-redis 依赖。
     * @param connectionFactory Redis 连接工厂
     * @return RedisTemplate 对象
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 Jackson2JsonRedisSerializer 序列化值  
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // Key 使用 String 序列化  
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 让 Spring Session 使用 JSON 方式存储
     * 配置后，Spring Session 会将 Session 数据序列化为 JSON 格式存储到 Redis 中，
     * 并在需要时反序列化为 Session 对象。
     * 这样可以更方便地管理和操作 Session 数据，同时也可以与其他 Spring 应用程序共享 Session 数据。
     * 注意：在使用 Spring Session 时，需要在 pom.xml 中添加 spring-session-data-redis 依赖。
     *
     * @return RedisSerializer 对象
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        // 让 Spring Session 使用 JSON 方式存储  
        return new GenericJackson2JsonRedisSerializer();
    }

}
