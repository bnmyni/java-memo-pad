package com.aspire.devops.report.dto;

/**
 * 登录请求
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dto
 * fileName: LoginRequest.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/08/08 10:31
 */
public class LoginRequest {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}