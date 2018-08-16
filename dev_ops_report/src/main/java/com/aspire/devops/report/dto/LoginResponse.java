package com.aspire.devops.report.dto;

/**
 * 登录返回
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dto
 * fileName: LoginResponse.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/08/08 10:30
 */
public class LoginResponse {

    private String token;

    private Boolean isLoin;

    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getLoin() {
        return isLoin;
    }

    public void setLoin(Boolean loin) {
        isLoin = loin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}