package com.aspire.devops.report.dto;

/**
 * 看板报表请求对象
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: ReportRequest.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/18 11:37
 */
public class ReportRequest {

    private Integer pid;

    private Integer sub_pid;

    private Integer years;

    private Integer status;

    private Integer limits;

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

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLimits() {
        return limits;
    }

    public void setLimits(Integer limits) {
        this.limits = limits;
    }
}