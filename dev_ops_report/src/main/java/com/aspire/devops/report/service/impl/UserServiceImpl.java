package com.aspire.devops.report.service.impl;

import com.aspire.devops.report.dao.UserDao;
import com.aspire.devops.report.dto.LoginResponse;
import com.aspire.devops.report.model.UserInfo;
import com.aspire.devops.report.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 系统用户Service实现
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service.impl
 * fileName: UserServiceImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/08/08 10:35
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    public UserInfo login(String username, String passwd) {

        return userDao.login(username, passwd);
    }
}