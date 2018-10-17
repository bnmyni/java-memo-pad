package com.aspire.dicmp.component.cache0;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.dicmp.component.cache0
 * fileName: App.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/12 17:00
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 是Spring Boot项目的核心注解,主要是开启自动配置
 */
@SpringBootApplication
@RestController
@EnableCaching
public class App {

    @Autowired
    private GuavaDataCache dataCache;

    @Autowired
    private RedisDataCache rdataCache;


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

    @RequestMapping("/putr")
    public String putr(Long id, String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date()) + " : value is " + rdataCache.put(id, value);
    }


    @RequestMapping("/getr")
    public String queryr(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date()) + " : value is " + rdataCache.query(id);
    }

    @RequestMapping("/remover")
    public String remover(Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rdataCache.remove(id);
        return sdf.format(new Date()) + " : success ";
    }

}