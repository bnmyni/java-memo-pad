package com.aspire;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire
 * fileName: SimpleApp.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/16 13:33
 */
public class SimpleApp {

    public static void main(String[] args) {

        AsyncLoadingCache<String, DataObject> cache = Caffeine.newBuilder()
                .maximumSize(100).expireAfterWrite(1, TimeUnit.MINUTES)
                .buildAsync(k -> DataObject.get("Data for " + k));

        System.out.println(cache.get("A"));
        cache.get("A").thenAccept(dataObject -> {
            System.out.println(dataObject.getData());
        });

        List<String> keys = new ArrayList<>();
        keys.add("V");
        keys.add("H");
        CompletableFuture<Map<String, DataObject>> graphs = cache.getAll(keys);

        graphs.thenAccept(stringDataObjectMap -> {
            System.out.println("CompletableFuture data..................");
            System.out.println("stringDataObjectMap:" + stringDataObjectMap.get("A").getData());
        });

        // 异步转同步
//        LoadingCache<String, DataObject> loadingCache = cache.synchronous();

    }

    /**
     * 同步获取缓存
     */
    public void loadingCache() {
        LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
                .maximumSize(100).expireAfterWrite(1, TimeUnit.MINUTES)
                .build(k -> DataObject.get("Data for " + k));

        // cache 里面是没有 a的
        System.out.println(cache.get("A").getData());
        Map<String, DataObject> map = cache.getAll(Arrays.asList("A", "B", "C"));
        map.forEach((k, v) -> System.out.println(k + ";" + v.getData()));
    }


    /**
     * 手动填值方式
     */
    public void manual() {
        Cache<String, DataObject> cache = Caffeine.newBuilder()
                .maximumSize(100).expireAfterWrite(1, TimeUnit.MINUTES).build();

        String key = "name";
        // 使用 getIfPresent 不存在的时候返回 null
        DataObject data = cache.getIfPresent(key);
        System.out.println(data);

        cache.put(key, new DataObject("aspire"));
        System.out.println(cache.getIfPresent(key).getData());

        // 使用 get（） 给出默认的值
        System.out.println(cache.get("age", this::getFun));
//        System.out.println(cache.get("age", k  -> getFun(k)));
    }

    private DataObject getFun(String key) {
        // 这里可以去查询数据库或者redis
        return new DataObject(key);
    }

}

class DataObject {
    private String data;

    private static int objectCounter = 0;

    public static DataObject get(String datas) {
        objectCounter++;
        return new DataObject(datas);
    }


    public DataObject(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static int getObjectCounter() {
        return objectCounter;
    }

    public static void setObjectCounter(int objectCounter) {
        DataObject.objectCounter = objectCounter;
    }
}