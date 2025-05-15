package com.ince.gigalike;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.ince.gigalike.mapper")
@EnableScheduling
public class GigaLikeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GigaLikeApplication.class, args);
    }

}
