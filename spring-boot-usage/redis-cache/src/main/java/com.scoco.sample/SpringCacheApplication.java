package com.scoco.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * spring cache示例代码启动类
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.sample.cache
 * fileName: SpringCacheApplication.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/11/08 17:12
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class SpringCacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringCacheApplication.class, args);
    }
}