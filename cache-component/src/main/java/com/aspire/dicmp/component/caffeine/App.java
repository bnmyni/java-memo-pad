package com.aspire.dicmp.component.caffeine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 用于测试caffeine cache
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.component.caffeine
 * fileName: App.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/15 10:31
 */
@SpringBootApplication
@EnableCaching
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}