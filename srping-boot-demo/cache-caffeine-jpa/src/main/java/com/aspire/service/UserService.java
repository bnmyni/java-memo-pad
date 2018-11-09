package com.aspire.service;

import com.aspire.entity.User;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.service
 * fileName: UserService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/10/17 00:54
 */
public interface UserService {

    void save(User user);

    void delete(Long id);

    User get(Long id);
}