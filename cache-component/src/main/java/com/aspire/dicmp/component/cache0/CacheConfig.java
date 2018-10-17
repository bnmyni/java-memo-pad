package com.aspire.dicmp.component.cache0;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.component.cache0
 * fileName: CacheConfig.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/12 16:40
 */
@Configuration
public class CacheConfig implements ApplicationRunner {

    @Resource
    private List<CacheManager> cacheManagers;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        System.out.println("start init cache");
        for (CacheManager cacheManager : cacheManagers) {
            System.out.println(cacheManager);
        }
    }

    @Bean(name = "redisCacheManager")
    public RedisCacheManager redisCacheManager() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        redisCacheManager.setCacheNames(Arrays.asList("redisDemo"));
        redisCacheManager.setUsePrefix(true);
        return redisCacheManager;
    }

    @Bean(name = "guavaCacheManager")
    public GuavaCacheManager getGuavaCacheManager() {

        GuavaCacheManager guavaCacheManager = new GuavaCacheManager();
        guavaCacheManager.setCacheBuilder(CacheBuilder.newBuilder()
                .expireAfterWrite(3600, TimeUnit.SECONDS).maximumSize(1000));
        ArrayList<String> guavaCacheNames = Lists.newArrayList();
        guavaCacheNames.add("guavaDemo");
        guavaCacheManager.setCacheNames(guavaCacheNames);
        return guavaCacheManager;
    }

    @Bean(name = "cacheManager")
    @Primary
    public CompositeCacheManager cacheManager(RedisCacheManager redisCacheManager,
                                              GuavaCacheManager guavaCacheManager) {
        return new CompositeCacheManager(redisCacheManager, guavaCacheManager);
    }

}