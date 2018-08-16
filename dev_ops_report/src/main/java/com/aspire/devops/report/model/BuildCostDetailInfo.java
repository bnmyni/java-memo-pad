package com.aspire.devops.report.model;

/**
 * 版本成本明细信息
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: BuildCostDetailInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/31 13:49
 */
public class BuildCostDetailInfo extends BuildCommonInfo {


    private Integer status;
    private Integer expectedWorkload;
    private Integer actualWorkload;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getExpectedWorkload() {
        return expectedWorkload;
    }

    public void setExpectedWorkload(Integer expectedWorkload) {
        this.expectedWorkload = expectedWorkload;
    }

    public Integer getActualWorkload() {
        return actualWorkload;
    }

    public void setActualWorkload(Integer actualWorkload) {
        this.actualWorkload = actualWorkload;
    }
}