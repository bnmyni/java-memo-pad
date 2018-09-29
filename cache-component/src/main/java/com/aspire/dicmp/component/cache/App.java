package com.aspire.dicmp.component.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 测试spring boot guava整合
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.component.cache
 * fileName: App.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/09/26 16:49
 */
@SpringBootApplication
@RestController
@EnableCaching
public class App {

    @Autowired
    private DataCache dataCache;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RequestMapping("/put")
    public String put(Long id, String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date()) + " : value is " + dataCache.put(id, value);
    }

    @RequestMapping("/get")
    public String query(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date()) + " : value is " + dataCache.query(id);
    }

    @RequestMapping("/remove")
    public String remove(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dataCache.remove(id);
        return sdf.format(new Date()) + " : success ";
    }
}