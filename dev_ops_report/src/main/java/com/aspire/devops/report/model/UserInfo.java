package com.aspire.devops.report.model;

/**
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: UserInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/08/08 10:41
 */
public class UserInfo {

    private Integer id;

    private String username;

    private String passwd;
    /**
     * 1:正常;2:停用;3:删除
     */
    private Integer status;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}