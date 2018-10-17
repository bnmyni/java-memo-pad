package com.aspire.cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.cache
 * fileName: CacheManagerCheck.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/15 18:15
 */
@Component
public class CacheManagerCheck implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CacheManagerCheck.class);
    private final CacheManager cacheManager;

    public CacheManagerCheck(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(String... strings) throws Exception {
        logger.info("\n\n" + "=========================================================\n"
                + "Using cache manager: " + this.cacheManager.getClass().getName() + "\n"
                + "=========================================================\n\n");
    }
}