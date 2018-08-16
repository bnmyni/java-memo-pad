package com.aspire.devops.report.model;

/**
 * 版本进度明细
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: BuildProgressDetailInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/31 10:33
 */
public class BuildProgressDetailInfo extends BuildCommonInfo {

    private Integer status;

    private String releasedDate;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }
}