package com.aspire.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import com.aspire.cache.layering.LayeringCacheManager;
import com.aspire.cache.setting.FirstCacheSetting;
import com.aspire.cache.setting.SecondaryCacheSetting;
import com.github.benmanes.caffeine.cache.CaffeineSpec;


@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    // redis缓存的有效时间单位是秒
    @Value("${redis.default.expiration:3600}")
    private long redisDefaultExpiration;

    // 查询缓存有效时间
    @Value("${select.cache.timeout:600}")
    private long selectCacheTimeout;

    // 查询缓存自动刷新时间
    @Value("${select.cache.refresh:580}")
    private long selectCacheRefresh;

    @Autowired
    private CacheProperties cacheProperties;

    @Bean
    @Primary
    public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
        LayeringCacheManager layeringCacheManager = new LayeringCacheManager(redisTemplate);
        setFirstCacheConfig(layeringCacheManager);
        setSecondaryCacheConfig(layeringCacheManager);
        // 允许存null，防止缓存击穿
        layeringCacheManager.setAllowNullValues(true);
        return layeringCacheManager;
    }

    private void setFirstCacheConfig(LayeringCacheManager layeringCacheManager) {
        // 设置默认的一级缓存配置
        String specification = this.cacheProperties.getCaffeine().getSpec();
        if (StringUtils.hasText(specification)) {
            layeringCacheManager.setCaffeineSpec(CaffeineSpec.parse(specification));
        }

        Map<String, FirstCacheSetting> firstCacheSettings = new HashMap<>();
        firstCacheSettings.put("user", new FirstCacheSetting("initialCapacity=5,maximumSize=500,expireAfterWrite=10s"));
        firstCacheSettings.put("people", new FirstCacheSetting("initialCapacity=5,maximumSize=50,expireAfterWrite=30s"));
        layeringCacheManager.setFirstCacheSettings(firstCacheSettings);
    }

    private void setSecondaryCacheConfig(LayeringCacheManager layeringCacheManager) {
        // 设置使用缓存名称（value属性）作为redis缓存前缀
        layeringCacheManager.setUsePrefix(true);
        layeringCacheManager.setSecondaryCacheDefaultExpiration(redisDefaultExpiration);

        // 设置每个二级缓存的过期时间和自动刷新时间
        Map<String, SecondaryCacheSetting> secondaryCacheSettings = new HashMap<>();
        secondaryCacheSettings.put("user", new SecondaryCacheSetting(selectCacheTimeout, selectCacheRefresh));
        secondaryCacheSettings.put("people", new SecondaryCacheSetting(5, 0, false, true));
        layeringCacheManager.setSecondaryCacheSettings(secondaryCacheSettings);
    }

    /**
     * 显示声明缓存key生成器
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

}
