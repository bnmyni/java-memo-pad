package com.aspire.cache.redis.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * 提供redis可选的一种序列化方式，但是该方式在序列化对象没有set方法的时候会报错
 *FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
 *全局开启AutoType，不建议使用
 *ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
 *fastjson在2017年3月爆出了在1.2.24以及之前版本存在远程代码执行高危安全漏洞。
 * 所以要使用ParserConfig.getGlobalInstance().addAccept("com.aspire.");指定序列化白名单。
 *ParserConfig.getGlobalInstance().addAccept("com.aspire.");
 * Copyright © 2018-2028 aspire Inc. All rights reserved.
 * package: com.aspire.cache.redis.serializer
 * fileName: FastJsonRedisSerializer.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/18 20:02
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final String EMPTY_OBJECT_FLAG = "EMPTY_OBJECT_FLAG_@$#";

    private Class<T> clazz;

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null || t instanceof NullValue) {
            // 如果是NULL,则存空对象标示
            return EMPTY_OBJECT_FLAG.getBytes(DEFAULT_CHARSET);
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        // 判断存储对象是否是NULL，是就返回null
        if (EMPTY_OBJECT_FLAG.equals(str)) {
            return null;
        }
        return (T) JSON.parseObject(str, clazz);
    }

}