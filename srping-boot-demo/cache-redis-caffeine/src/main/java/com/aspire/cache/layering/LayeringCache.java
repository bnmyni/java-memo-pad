package com.aspire.cache.layering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.core.RedisOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import com.alibaba.fastjson.JSON;
import com.aspire.cache.enums.ChannelTopicEnum;
import com.aspire.cache.listener.RedisPublisher;
import com.aspire.cache.redis.cache.CustomizedRedisCache;


public class LayeringCache extends AbstractValueAdaptingCache {
    private Logger logger = LoggerFactory.getLogger(LayeringCache.class);

    /**
     * 缓存的名称
     */
    private final String name;

    /**
     * 是否使用一级缓存
     */
    private boolean usedFirstCache = true;

    /**
     * redi缓存
     */
    private final CustomizedRedisCache redisCache;

    /**
     * Caffeine缓存
     */
    private final CaffeineCache caffeineCache;

    RedisOperations<? extends Object, ? extends Object> redisOperations;

    /**
     * @param name              缓存名称
     * @param prefix            缓存前缀
     * @param redisOperations   操作Redis的RedisTemplate
     * @param expiration        redis缓存过期时间
     * @param preloadSecondTime redis缓存自动刷新时间
     * @param allowNullValues   是否允许存NULL，默认是false
     * @param usedFirstCache    是否使用一级缓存，默认是true
     * @param forceRefresh      是否强制刷新（走数据库），默认是false
     * @param caffeineCache     Caffeine缓存
     */
    public LayeringCache(String name, byte[] prefix, RedisOperations<? extends Object, ? extends Object> redisOperations,
                         long expiration, long preloadSecondTime, boolean allowNullValues, boolean usedFirstCache,
                         boolean forceRefresh, com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache) {

        super(allowNullValues);
        this.name = name;
        this.usedFirstCache = usedFirstCache;
        this.redisOperations = redisOperations;
        this.redisCache = new CustomizedRedisCache(name, prefix, redisOperations, expiration, preloadSecondTime, forceRefresh, allowNullValues);
        this.caffeineCache = new CaffeineCache(name, caffeineCache, allowNullValues);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    public CustomizedRedisCache getSecondaryCache() {
        return this.redisCache;
    }

    public CaffeineCache getFirstCache() {
        return this.caffeineCache;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper wrapper = null;
        if (usedFirstCache) {
            wrapper = caffeineCache.get(key);
            logger.debug("查询一级缓存 key:{},返回值是:{}", key, JSON.toJSONString(wrapper));
        }

        if (wrapper == null) {
            wrapper = redisCache.get(key);
            caffeineCache.put(key, wrapper == null ? null : wrapper.get());
            logger.debug("查询二级缓存,并将数据放到一级缓存。 key:{},返回值是:{}", key, JSON.toJSONString(wrapper));
        }
        return wrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = null;
        if (usedFirstCache) {
            value = caffeineCache.get(key, type);
            logger.debug("查询一级缓存 key:{},返回值是:{}", key, JSON.toJSONString(value));
        }

        if (value == null) {
            value = redisCache.get(key, type);
            caffeineCache.put(key, value);
            logger.debug("查询二级缓存,并将数据放到一级缓存。 key:{},返回值是:{}", key, JSON.toJSONString(value));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = null;
        if (usedFirstCache) {
            value = (T) caffeineCache.getNativeCache().get(key, k -> getForSecondaryCache(k, valueLoader));
        } else {
            value = (T) getForSecondaryCache(key, valueLoader);
        }

        if (value instanceof NullValue) {
            return null;
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        if (usedFirstCache) {
            caffeineCache.put(key, value);
        }
        redisCache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (usedFirstCache) {
            caffeineCache.putIfAbsent(key, value);
        }
        return redisCache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        redisCache.evict(key);
        if (usedFirstCache) {
            Map<String, Object> message = new HashMap<>();
            message.put("cacheName", name);
            message.put("key", key);
            RedisPublisher redisPublisher = new RedisPublisher(redisOperations, ChannelTopicEnum.REDIS_CACHE_DELETE_TOPIC.getChannelTopic());
            redisPublisher.publisher(message);
        }
    }

    @Override
    public void clear() {
        redisCache.clear();
        if (usedFirstCache) {
            Map<String, Object> message = new HashMap<>();
            message.put("cacheName", name);
            RedisPublisher redisPublisher = new RedisPublisher(redisOperations, ChannelTopicEnum.REDIS_CACHE_CLEAR_TOPIC.getChannelTopic());
            redisPublisher.publisher(message);
        }
    }

    @Override
    protected Object lookup(Object key) {
        Object value = null;
        if (usedFirstCache) {
            value = caffeineCache.get(key);
            logger.debug("查询一级缓存 key:{},返回值是:{}", key, JSON.toJSONString(value));
        }
        if (value == null) {
            value = redisCache.get(key);
            logger.debug("查询二级缓存 key:{},返回值是:{}", key, JSON.toJSONString(value));
        }
        return value;
    }

    /**
     * 查询二级缓存
     *
     * @param key
     * @param valueLoader
     * @return
     */
    private <T> Object getForSecondaryCache(Object key, Callable<T> valueLoader) {
        T value = redisCache.get(key, valueLoader);
        logger.debug("查询二级缓存 key:{},返回值是:{}", key, JSON.toJSONString(value));
        return toStoreValue(value);
    }
}
