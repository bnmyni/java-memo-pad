package com.aspire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire
 * fileName: SpringBootCacheCaffeineApplication.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/17 00:27
 */
@SpringBootApplication
@EnableCaching
public class SpringBootCacheCaffeineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCacheCaffeineApplication.class, args);
    }
}