package com.aspire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import com.aspire.dao.UserRepository;
import com.aspire.entity.User;
import com.aspire.service.UserService;

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
    private UserService userService;

    @PostMapping(path = "save")
    public void save(@RequestBody User user) {
        System.out.println(user);
        userService.save(user);
    }

    @DeleteMapping(path = "del/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping(path = "query/{id}")
    @ResponseBody
    public User get(@PathVariable Long id) {
        System.out.println(new Date() + "get user by id :" + id);
        return userService.get(id);
    }
}