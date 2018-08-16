package com.aspire.devops.report.model;

/**
 * 项目信息
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: ProjectInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/19 10:19
 */
public class ProjectInfo {

    private Long pid;
    private Long subPid;
    private String pname;
    private String subPname;
    private Integer type;

    public ProjectInfo() {
    }

    public ProjectInfo(Long pid, Long subPid, String pname, String subPname, Integer type) {
        this.pid = pid;
        this.subPid = subPid;
        this.pname = pname;
        this.subPname = subPname;
        this.type = type;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getSubPid() {
        return subPid;
    }

    public void setSubPid(Long subPid) {
        this.subPid = subPid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getSubPname() {
        return subPname;
    }

    public void setSubPname(String subPname) {
        this.subPname = subPname;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}