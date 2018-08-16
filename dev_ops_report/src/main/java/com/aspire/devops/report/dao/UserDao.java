package com.aspire.devops.report.dao;

import com.aspire.devops.report.model.UserInfo;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dao
 * fileName: UserDao.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/08/08 10:37
 */
public interface UserDao {

    /**
     * 用户登录
     * @param username 用户名
     * @param passwd  密码
     * @return 查询用户是否存在
     */
    UserInfo login(String username, String passwd);
}