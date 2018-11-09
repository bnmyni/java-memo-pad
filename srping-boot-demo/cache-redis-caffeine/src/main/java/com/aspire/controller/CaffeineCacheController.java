package com.aspire.controller;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.*;
import com.google.common.testing.FakeTicker;
import com.aspire.entity.Person;
import com.aspire.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@RestController
public class CaffeineCacheController {

    @Autowired
    PersonService personService;

    Cache<String, Person> manualCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    LoadingCache<String, Person> loadingCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(this::createExpensiveGraph);

    AsyncLoadingCache<String, Person> asyncLoadingCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .buildAsync(this::createExpensiveGraph);


    private Person createExpensiveGraph(String key) {
        // TODO 一级缓存没有到二级缓存中找，如果二级缓存里面也咩有就到db中到
        return personService.query();
    }

    @RequestMapping("/testManual")
    public Person testManual(@RequestBody Person person) {
        String key = "people" + person.getId();
        Person graph = manualCache.get(key, this::createExpensiveGraph);
        manualCache.put(key, new Person());
        manualCache.invalidate(key);
        ConcurrentMap<String, Person> map = manualCache.asMap();
        map.forEach((k, v) -> System.out.println(k));
        return graph;
    }

    @RequestMapping("/testTimeBased")
    public Object testTimeBased(Person person) {
        String key = "name1";
        FakeTicker ticker = new FakeTicker();

        LoadingCache<String, Object> cache1 = Caffeine.newBuilder()
                .ticker(ticker::read)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .build(k -> createExpensiveGraph(k));

        System.out.println("expireAfterAccess：第一次获取缓存");
        cache1.get(key);

        System.out.println("expireAfterAccess：等待4.9S后，第二次次获取缓存");
        // 直接指定时钟
        ticker.advance(4900, TimeUnit.MILLISECONDS);
        cache1.get(key);

        System.out.println("expireAfterAccess：等待0.101S后，第三次次获取缓存");
        ticker.advance(101, TimeUnit.MILLISECONDS);
        cache1.get(key);

        // expireAfterWrite
        LoadingCache<String, Object> cache2 = Caffeine.newBuilder()
                .ticker(ticker::read)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build(k -> createExpensiveGraph(k));

        System.out.println("expireAfterWrite：第一次获取缓存");
        cache2.get(key);

        System.out.println("expireAfterWrite：等待4.9S后，第二次次获取缓存");
        ticker.advance(4900, TimeUnit.MILLISECONDS);
        cache2.get(key);

        System.out.println("expireAfterWrite：等待0.101S后，第三次次获取缓存");
        ticker.advance(101, TimeUnit.MILLISECONDS);
        cache2.get(key);

        // Evict based on a varying expiration policy
        // 基于不同的到期策略进行退出
        LoadingCache<String, Object> cache3 = Caffeine.newBuilder()
                .ticker(ticker::read)
                .expireAfter(new Expiry<String, Object>() {

                    @Override
                    public long expireAfterCreate(String key, Object value, long currentTime) {
                        // Use wall clock time, rather than nanotime, if from an external resource
                        return TimeUnit.SECONDS.toNanos(5);
                    }

                    @Override
                    public long expireAfterUpdate(String key, Object graph,
                                                  long currentTime, long currentDuration) {

                        System.out.println("调用了 expireAfterUpdate：" + TimeUnit.NANOSECONDS.toMillis(currentDuration));
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(String key, Object graph,
                                                long currentTime, long currentDuration) {

                        System.out.println("调用了 expireAfterRead：" + TimeUnit.NANOSECONDS.toMillis(currentDuration));
                        return currentDuration;
                    }
                })
                .build(k -> createExpensiveGraph(k));

        System.out.println("expireAfter：第一次获取缓存");
        cache3.get(key);

        System.out.println("expireAfter：等待4.9S后，第二次次获取缓存");
        ticker.advance(4900, TimeUnit.MILLISECONDS);
        cache3.get(key);

        System.out.println("expireAfter：等待0.101S后，第三次次获取缓存");
        ticker.advance(101, TimeUnit.MILLISECONDS);
        Object object = cache3.get(key);

        return object;
    }

    @RequestMapping("/testRemoval")
    public Object testRemoval(Person person) {
        String key = "name1";
        // 用户测试，一个时间源，返回一个时间值，表示从某个固定但任意时间点开始经过的纳秒数。
        FakeTicker ticker = new FakeTicker();

        // 基于固定的到期策略进行退出
        // expireAfterAccess
        LoadingCache<String, Object> cache = Caffeine.newBuilder()
                .removalListener((String k, Object graph, RemovalCause cause) ->
                        System.out.printf("Key %s was removed (%s)%n", k, cause))
                .ticker(ticker::read)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .build(k -> createExpensiveGraph(k));

        System.out.println("第一次获取缓存");
        Object object = cache.get(key);

        System.out.println("等待6S后，第二次次获取缓存");
        // 直接指定时钟
        ticker.advance(6000, TimeUnit.MILLISECONDS);
        cache.get(key);

        System.out.println("手动删除缓存");
        cache.invalidate(key);

        return object;
    }

    @RequestMapping("/testRefresh")
    public Object testRefresh(Person person) {
        String key = "name1";
        // 用户测试，一个时间源，返回一个时间值，表示从某个固定但任意时间点开始经过的纳秒数。
        FakeTicker ticker = new FakeTicker();

        // 基于固定的到期策略进行退出
        // expireAfterAccess
        LoadingCache<String, Object> cache = Caffeine.newBuilder()
                .removalListener((String k, Object graph, RemovalCause cause) ->
                        System.out.printf("执行移除监听器- Key %s was removed (%s)%n", k, cause))
                .ticker(ticker::read)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                // 指定在创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存
                .refreshAfterWrite(4, TimeUnit.SECONDS)
                .build(k -> createExpensiveGraph(k));

        System.out.println("第一次获取缓存");
        Object object = cache.get(key);

        System.out.println("等待4.1S后，第二次次获取缓存");
        // 直接指定时钟
        ticker.advance(4100, TimeUnit.MILLISECONDS);
        cache.get(key);

        System.out.println("等待5.1S后，第三次次获取缓存");
        // 直接指定时钟
        ticker.advance(5100, TimeUnit.MILLISECONDS);
        cache.get(key);

        return object;
    }

    @RequestMapping("/testWriter")
    public Object testWriter(Person person) {
        String key = "name1";
        // 用户测试，一个时间源，返回一个时间值，表示从某个固定但任意时间点开始经过的纳秒数。
        FakeTicker ticker = new FakeTicker();

        // 基于固定的到期策略进行退出
        // expireAfterAccess
        LoadingCache<String, Object> cache = Caffeine.newBuilder()
                .removalListener((String k, Object graph, RemovalCause cause) ->
                        System.out.printf("执行移除监听器- Key %s was removed (%s)%n", k, cause))
                .ticker(ticker::read)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .writer(new CacheWriter<String, Object>() {
                    @Override
                    public void write(String key, Object graph) {
                        // write to storage or secondary cache
                        // 写入存储或者二级缓存
                        System.out.printf("testWriter:write - Key %s was write (%s)%n", key, graph);
                        createExpensiveGraph(key);
                    }

                    @Override
                    public void delete(String key, Object graph, RemovalCause cause) {
                        // delete from storage or secondary cache
                        // 删除存储或者二级缓存
                        System.out.printf("testWriter:delete - Key %s was delete (%s)%n", key, graph);
                    }
                })
                // 指定在创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存
                .refreshAfterWrite(4, TimeUnit.SECONDS)
                .build(k -> createExpensiveGraph(k));

        cache.put(key, personService.query());
        cache.invalidate(key);

        System.out.println("第一次获取缓存");
        Object object = cache.get(key);

        System.out.println("等待4.1S后，第二次次获取缓存");
        // 直接指定时钟
        ticker.advance(4100, TimeUnit.MILLISECONDS);
        cache.get(key);

        System.out.println("等待5.1S后，第三次次获取缓存");
        // 直接指定时钟
        ticker.advance(5100, TimeUnit.MILLISECONDS);
        cache.get(key);

        return object;
    }

    @RequestMapping("/testStatistics")
    public Object testStatistics(Person person) {
        String key = "name1";
        // 用户测试，一个时间源，返回一个时间值，表示从某个固定但任意时间点开始经过的纳秒数。
        FakeTicker ticker = new FakeTicker();

        // 基于固定的到期策略进行退出
        // expireAfterAccess
        LoadingCache<String, Object> cache = Caffeine.newBuilder()
                .removalListener((String k, Object graph, RemovalCause cause) ->
                        System.out.printf("执行移除监听器- Key %s was removed (%s)%n", k, cause))
                .ticker(ticker::read)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                // 开启统计
                .recordStats()
                // 指定在创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存
                .refreshAfterWrite(4, TimeUnit.SECONDS)
                .build(k -> createExpensiveGraph(k));

        for (int i = 0; i < 10; i++) {
            cache.get(key);
            cache.get(key + i);
        }
        // 驱逐是异步操作，所以这里要手动触发一次回收操作
        ticker.advance(5100, TimeUnit.MILLISECONDS);
        // 手动触发一次回收操作
        cache.cleanUp();

        System.out.println("缓存命数量：" + cache.stats().hitCount());
        System.out.println("缓存命中率：" + cache.stats().hitRate());
        System.out.println("缓存逐出的数量：" + cache.stats().evictionCount());
        System.out.println("加载新值所花费的平均时间：" + cache.stats().averageLoadPenalty());

        return cache.get(key);
    }

    @RequestMapping("/testPolicy")
    public Object testPolicy(Person person) {
        FakeTicker ticker = new FakeTicker();

        LoadingCache<String, Object> cache = Caffeine.newBuilder()
                .ticker(ticker::read)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .maximumSize(1)
                .build(k -> createExpensiveGraph(k));

        // 在代码里面动态的指定最大Size
        cache.policy().eviction().ifPresent(eviction -> {
            eviction.setMaximum(4 * eviction.getMaximum());
        });

        cache.get("E");
        cache.get("B");
        cache.get("C");
        cache.cleanUp();
        System.out.println(cache.estimatedSize() + ":" + JSON.toJSON(cache.asMap()).toString());

        cache.get("A");
        ticker.advance(100, TimeUnit.MILLISECONDS);
        cache.get("D");
        ticker.advance(100, TimeUnit.MILLISECONDS);
        cache.get("A");
        ticker.advance(100, TimeUnit.MILLISECONDS);
        cache.get("B");
        ticker.advance(100, TimeUnit.MILLISECONDS);
        cache.policy().eviction().ifPresent(eviction -> {
            // 获取热点数据Map
            Map<String, Object> hottestMap = eviction.hottest(10);
            // 获取冷数据Map
            Map<String, Object> coldestMap = eviction.coldest(10);

            System.out.println("热点数据:" + JSON.toJSON(hottestMap).toString());
            System.out.println("冷数据:" + JSON.toJSON(coldestMap).toString());
        });

        ticker.advance(3000, TimeUnit.MILLISECONDS);
        // ageOf通过这个方法来查看key的空闲时间
        cache.policy().expireAfterAccess().ifPresent(expiration -> {

            System.out.println(JSON.toJSON(expiration.ageOf("A", TimeUnit.MILLISECONDS)));
        });
        return cache.get("name1");
    }


}