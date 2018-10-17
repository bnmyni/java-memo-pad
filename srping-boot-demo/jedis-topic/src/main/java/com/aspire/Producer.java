package com.aspire;

import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire
 * fileName: Producer.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/16 11:24
 */
public class Producer {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("10.12.70.29", 6380);
        Long publishCount = jedis.publish("test1", "this is new message" + new Date());
        jedis.publish("test1", "close now");
        System.out.println("test1 订阅者数量为:" + publishCount);

    }
}