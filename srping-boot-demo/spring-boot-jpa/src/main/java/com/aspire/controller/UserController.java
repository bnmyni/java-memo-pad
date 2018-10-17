package com.aspire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.dao.UserRepository;
import com.aspire.entity.User;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.controller
 * fileName: UserController.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/17 00:21
 */
@RestController
@RequestMapping(value = "user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "save")
    public void save(@RequestBody User user) {
        System.out.println(user);
        userRepository.save(user);
    }

    @DeleteMapping(path = "del/{id}")
    public void delete(@PathVariable Long id) {
        userRepository.delete(id);
    }
}