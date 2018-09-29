package com.aspire.dicmp.component.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * guava cache使用实例
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.component.cache
 * fileName: GuavaCacheUse.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/09/21 09:04
 */
public class GuavaCacheUse {

    public static void main(String[] args) {
        CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                if ("sky".equals(key)) {
                    return "aspire";
                }
                return null;
            }
        });
    }
}