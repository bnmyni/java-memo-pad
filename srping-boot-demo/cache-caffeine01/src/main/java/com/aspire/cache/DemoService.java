package com.aspire.cache;

import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.cache
 * fileName: DemoService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/15 18:16
 */
@Service
public class DemoService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DemoService.class);

    @Cacheable(value = "getDefault")
    public String getId(String id) {
        logger.info("get id from db**********{}", id);
        return id;

    }
}