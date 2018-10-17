package com.aspire;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire
 * fileName: SpringCacheApplication.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/15 18:19
 */
@EnableCaching
@SpringBootApplication
public class SpringCacheApplication {
    private static Logger log = LogManager.getLogger(SpringCacheApplication.class);

    public static void main(String[] args) {
//        new SpringApplicationBuilder(SpringCacheApplication.class).bannerMode(Banner.Mode.OFF).run(args);
        SpringApplication.run(SpringCacheApplication.class, args);
    }
}