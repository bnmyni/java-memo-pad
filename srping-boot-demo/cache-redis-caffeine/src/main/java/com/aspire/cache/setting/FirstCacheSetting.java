package com.aspire.cache.setting;

import com.github.benmanes.caffeine.cache.CaffeineSpec;

/**
 * 一级缓存配置
 * Copyright © 2018-2028 aspire Inc. All rights reserved.
 * package: com.aspire.cache.setting
 * fileName: FirstCacheSetting.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/18 18:30
 */
public class FirstCacheSetting {

    /**
     * 一级缓存配置，配置项请点击这里 {@link CaffeineSpec#configure(String, String)}
     * @param cacheSpecification
     */
    public FirstCacheSetting(String cacheSpecification) {
        this.cacheSpecification = cacheSpecification;
    }

    private String cacheSpecification;

    public String getCacheSpecification() {
        return cacheSpecification;
    }
}
