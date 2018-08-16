package com.aspire.devops.report.vo;

/**
 * 版本进度查询DAO VO
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.vo
 * fileName: ProgressVo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/24 21:55
 */
public class ProgressVo {

    private Integer pid;

    private Integer sub_pid;

    private String startDate;

    private String endDate;

    private Integer status;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getSub_pid() {
        return sub_pid;
    }

    public void setSub_pid(Integer sub_pid) {
        this.sub_pid = sub_pid;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}