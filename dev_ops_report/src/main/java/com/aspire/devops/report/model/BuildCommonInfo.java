package com.aspire.devops.report.model;

/**
 * 版本通用信息
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: BuildCommonInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/31 14:18
 */
public class BuildCommonInfo {

    private Integer pid;
    private Integer subPid;
    private String pname;
    private String subPname;
    private String num;
    private String startDate;
    private String endDate;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getSubPid() {
        return subPid;
    }

    public void setSubPid(Integer subPid) {
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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}