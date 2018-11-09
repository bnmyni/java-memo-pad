package com.aspire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
/**
 *
 * Copyright © 2018-2028 aspire Inc. All rights reserved.
 * package: com.aspire
 * fileName: SpringBootCacheRedisApplication.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/18 16:02
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class SpringBootCacheRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootCacheRedisApplication.class, args);
	}
}
