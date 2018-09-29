package com.aspire.dicmp.component.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

/**
 * 测试Guava数据缓存
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.component.cache
 * fileName: DataCache.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/09/26 16:39
 */
@Component
public class DataCache {

    private Map<Long, String> dataMap = new HashMap<Long, String>();

    @PostConstruct
    public void init() {
        dataMap.put(1L, "sunke");
        dataMap.put(2L, "aspire");
        dataMap.put(3L, "china");
    }

    @Cacheable(value = "aspire", key = "#id + 'dataMap'", condition = "#id < 7")
    public String query(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()) + " : query id is " + id);
        return dataMap.get(id);
    }

    @CachePut(value = "aspire", key = "#id + 'dataMap'")
    public String put(Long id, String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()) + " : add data ,id is " + id);
        dataMap.put(id, value);
        return value;
    }

    @CacheEvict(value = "aspire", key = "#id + 'dataMap'")
    public void remove(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()) + " : remove id is " + id + " data");
        dataMap.remove(id);
    }


}