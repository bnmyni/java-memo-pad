package com.aspire.dicmp.component.cache0;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;


@Service
//@CacheConfig(cacheManager = "guavaCacheManager")
public class GuavaDataCache {

    private Map<Long, String> dataMap = new HashMap<Long, String>();

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        dataMap.put(1L, "张三");
        dataMap.put(2L, "李四");
        dataMap.put(3L, "王五");
    }


    /**
     * 查询
     * <p>
     * 如果数据没有缓存,那么从dataMap里面获取,如果缓存了,
     * <p>
     * 那么从guavaDemo里面获取
     * <p>
     * 并且将缓存的数据存入到 guavaDemo里面
     * <p>
     * 其中key 为 #id+dataMap
     */

    @Cacheable(value = "guavaDemo", key = "#id + 'dataMap'")
    public String query(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()) + " : query id is " + id);
        return dataMap.get(id);
    }


    /**
     * 插入 或者更新
     * <p>
     * 插入或更新数据到dataMap中
     * <p>
     * 并且缓存到 guavaDemo中
     * <p>
     * 如果存在了那么更新缓存中的值
     * <p>
     * 其中key 为 #id+dataMap
     */

    @CachePut(value = "guavaDemo", key = "#id + 'dataMap'")
    public String put(Long id, String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()) + " : add data ,id is " + id);
        dataMap.put(id, value);
        return value;
    }


    /**
     * 删除
     * <p>
     * 删除dataMap里面的数据
     * <p>
     * 并且删除缓存guavaDemo中的数据
     * <p>
     * 其中key 为 #id+dataMap
     */
    @CacheEvict(value = "guavaDemo", key = "#id + 'dataMap'")
    public void remove(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()) + " : remove id is " + id + " data");
        dataMap.remove(id);
    }

}