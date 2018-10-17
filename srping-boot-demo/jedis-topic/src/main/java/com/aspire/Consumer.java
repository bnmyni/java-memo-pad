package com.aspire;

import redis.clients.jedis.Jedis;

import com.aspire.clients.MessageHandler;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire
 * fileName: Consumer.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/16 11:31
 */
public class Consumer {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("10.12.70.29", 6380);
        MessageHandler handler = new MessageHandler();
        jedis.subscribe(handler, "test1");
    }
}