package com.scoco.sample.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import com.alibaba.fastjson.JSON;

/**
 * 重写StringRedisSerializer解决redisTemplate在序列化key是报错的问题,如果强制redis的key为String类型，则无需重写
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.j2cache.cache.redis.serializer
 * fileName: StringRedisSerializer.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/19 17:06
 */
public class StringRedisSerializer implements RedisSerializer<Object> {

    private final Charset charset;

    private final String target = "\"";

    private final String replacement = "";

    public StringRedisSerializer() {
        this(Charset.forName("UTF8"));
    }

    public StringRedisSerializer(Charset charset) {
        Assert.notNull(charset, "Charset must not be null!");
        this.charset = charset;
    }

    @Override
    public String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, charset));
    }

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        String string = JSON.toJSONString(object);
        string = string.replace(target, replacement);
        return string.getBytes(charset);
    }
}
