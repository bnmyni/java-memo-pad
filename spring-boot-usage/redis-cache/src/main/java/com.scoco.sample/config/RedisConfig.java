package com.scoco.sample.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import com.scoco.sample.enums.CacheEnum;
import com.scoco.sample.serializer.KryoRedisSerializer;
import com.scoco.sample.serializer.StringRedisSerializer;


/**
 * redis配置,修改RedisTemplate序列化方式
 * package: com.scoco.sample.sample.config
 * fileName: RedisConfig.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/19 16:54
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        KryoRedisSerializer<Object> kryoRedisSerializer = new KryoRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(kryoRedisSerializer);
        redisTemplate.setHashValueSerializer(kryoRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> config = new HashMap<>();
        for (CacheEnum cache : CacheEnum.values()) {
            System.out.printf("init cache %s, ttl is %s", cache.name(), cache.getTtl());
            config.put(cache.name(), RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofSeconds(cache.getTtl()))
                    .disableCachingNullValues().prefixKeysWith(cache.getPrefixKey()));
        }
        return RedisCacheManager.builder(redisConnectionFactory)
                .withInitialCacheConfigurations(config).transactionAware().build();
    }


}
