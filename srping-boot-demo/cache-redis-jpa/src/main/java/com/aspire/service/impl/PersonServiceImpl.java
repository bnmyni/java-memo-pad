package com.aspire.service.impl;

import com.aspire.entity.Person;
import com.aspire.repository.PersonRepository;
import com.aspire.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright © 2018-2028 aspire Inc. All rights reserved.
 * package: com.aspire.service.impl
 * fileName: PersonServiceImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/18 18:02
 */
@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    PersonRepository personRepository;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 数据保存到数据库并放入到缓存中
     * @param person 用户信息
     * @return 用户信息
     */
    @Override
    @CachePut(value = "people", key = "#person.id")
    public Person save(Person person) {
        return personRepository.save(person);
    }

    /**
     * 根据id删除缓存中用户
     */
    @Override
    @CacheEvict(value = "people", key ="#person.id" )
    public void remove(Person person) {

    }

    /**
     * sync：设置如果缓存过期是不是只放一个请求去请求数据库，其他请求阻塞，默认是false。
     * value 可以通过# 设置三个参数，第一个是key前缀，第二个是过期时间，第三个是自动刷新时间
     * 对于redis缓存而言，自动刷新时间是没有用的
     */
    @Override
//    @Cacheable(value = "people#${server.port2:30}#${spring.datasource.dbcp2.max-idle2:20}", key = "#person.id")
    @Cacheable(value = "people#30#5", key = "#person.id")
    public Person findOne(Person person) {
        System.out.println("开始查询了 ...");
        return personRepository.findOne(person.getId());
    }

}
