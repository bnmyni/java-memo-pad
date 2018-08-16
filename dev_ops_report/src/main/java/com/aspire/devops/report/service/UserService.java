package com.aspire.devops.report.service;

import com.aspire.devops.report.model.UserInfo;

/**
 * 系统用户Service
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service
 * fileName: UserService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/08/08 10:34
 */
public interface UserService {

    UserInfo login(String username, String passwd);
}