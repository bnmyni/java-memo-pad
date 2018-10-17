package com.aspire.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.aspire.dao.UserRepository;
import com.aspire.entity.User;
import com.aspire.service.UserService;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.service.impl
 * fileName: UserServiceImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/17 00:55
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @CachePut(value="user", key = "#user.id")
    public void save(User user) {
        User u = userRepository.save(user);
        System.out.println("save user and return user id is :" + u.getId());
    }

    @Override
    @CacheEvict(value = "user")
    public void delete(Long id) {
        System.out.println("save user....");
        userRepository.delete(id);
    }

    /**
     * sync：设置如果缓存过期是不是只放一个请求去请求数据库，其他请求阻塞，默认是false。
     */
    @Override
    @Cacheable(value = "user", key = "#id")
    public User get(Long id) {
        System.out.println("get user for db and user id is :" + id);
        return userRepository.getOne(id);
    }
}