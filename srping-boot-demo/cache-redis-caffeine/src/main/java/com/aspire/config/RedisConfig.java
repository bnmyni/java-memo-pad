package com.aspire.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.aspire.cache.enums.ChannelTopicEnum;
import com.aspire.cache.redis.serializer.KryoRedisSerializer;
import com.aspire.cache.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public KryoRedisSerializer<Object> kryoRedisSerializer() {
        return new KryoRedisSerializer<>(Object.class);
    }

    /**
     * 设置RedisTemplate的序列化方式
     * @param redisConnectionFactory redisConnectionFactory
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setValueSerializer(kryoRedisSerializer());
        redisTemplate.setHashValueSerializer(kryoRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter messageListener) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener, ChannelTopicEnum.REDIS_CACHE_DELETE_TOPIC.getChannelTopic());
        container.addMessageListener(messageListener, ChannelTopicEnum.REDIS_CACHE_CLEAR_TOPIC.getChannelTopic());
        return container;
    }

}
