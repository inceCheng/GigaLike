package com.ince.gigalike;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ince.gigalike.mapper")
public class GigaLikeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GigaLikeApplication.class, args);
    }

}
